/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.session;

import java.util.ArrayDeque;

import com.brew.brewpot.BrewPot;
import com.brew.brewpot.BrewPotConfig;
import com.brew.brewpot.BrewPotConfig.Mode;
import com.brew.fermenter.Fermenter;
import com.brew.fermenter.FermenterConfig;
import com.brew.fermenter.FermenterState;
import com.brew.notify.Event;
import com.brew.notify.Listener;
import com.brew.notify.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrew.p.davis
 */
public class SessionManager {

    private static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);

    private ArrayDeque<SessionConfig> priorConfigs = new ArrayDeque<SessionConfig>();
    
    private Session session;
    private BrewPot brewPot;

    public static final String EVENT_SESSION_CONFIG = "SESSION.CONFIG";



    public SessionManager() {
        //Temporary
        

    }

    public BrewPot getBrewPot() {
        return brewPot;
    }

    public void setBrewPot(BrewPot brewPot) {
        this.brewPot = brewPot;
    }

    public void startNewSession() {
        session = new Session();
        session.setConfig(new SessionConfig());
        session.getConfig().setPhase(SessionConfig.Phase.RECIPE);
        session.getConfig().setMode(SessionConfig.Mode.PAUSED);
        session.getConfig().setPhaseTime(System.currentTimeMillis());

        //Register listeners or do it when the state changes..?
        //Add for now for testing -- Might need to save these off if we ever want to de-register..
        Notifier.registerListener(Fermenter.EVENT_FERM_STATE, (Listener<FermenterState>) (Event<FermenterState> event) -> {
            session.addFermState(event);
        });

        Notifier.registerListener(Fermenter.EVENT_FERM_CONFIG, (Listener<FermenterConfig>) (Event<FermenterConfig> event) -> {
            session.addFermConfig(event);
        });


    }

    public Session getCurrentSession() {
        return this.session;
    }


    public synchronized void undoConfig() {
        LOG.info("Reverting configuration");
        if( this.priorConfigs.size() > 0 ) {
            updateConfig(this.priorConfigs.pop(), true);
        }
    }

    public synchronized void updateConfig(SessionConfig newConfig) {
        updateConfig(newConfig, false);
    }
    
    private void updateConfig(SessionConfig newConfig, boolean isUndo) {

        SessionConfig oldConfig = this.session.getConfig();

        //Is this a configuration change?
        if( newConfig != null && !newConfig.compare(oldConfig) ) {
            LOG.info("Updating configuration: {} --> {}", oldConfig, newConfig);
            session.setConfig(newConfig);
            
            if( !isUndo ) {
                //Don't reset time in case of old config
                session.getConfig().setPhaseTime(System.currentTimeMillis());
                priorConfigs.push(oldConfig.duplicate());
            }

            //NOTIFY
            com.brew.notify.Notifier.notifyListeners(new Event<SessionConfig> (EVENT_SESSION_CONFIG, this.session.getConfig().duplicate()));
        }


        //Apply configuration change to burner
        
        if( session.getConfig().getMode() == SessionConfig.Mode.ACTIVE ) {

            switch( session.getConfig().getPhase()) {
                case BREW_PREMASH : {
                    //Burner Target 
                    //TODO LOAD THIS FROM RECIPE
                    brewPot.updateConfig(new BrewPotConfig(166.6f, BrewPotConfig.Mode.HIGH));
                    break;
                }
                case BREW_MASH : {
                    //Burner Target and Low Duty
                    brewPot.updateConfig(new BrewPotConfig(156f, BrewPotConfig.Mode.LOW));
                    break;
                }
                case BREW_PREBOIL : {
                    //Burner ON 
                    brewPot.updateConfig(new BrewPotConfig(212f, BrewPotConfig.Mode.HIGH));
                    break;
                }
                case BREW_BOIL : {
                    //Burner Duty 50% 
                    brewPot.updateConfig(new BrewPotConfig(212f, BrewPotConfig.Mode.MED));
                    break;
                }
                default: {
                    //Turn off
                    BrewPotConfig config = brewPot.getConfig();
                    config.setMode(BrewPotConfig.Mode.OFF);
                    brewPot.updateConfig(config);
                }
            }

        } else {
            //Turn off
            BrewPotConfig config = brewPot.getConfig();
            config.setMode(BrewPotConfig.Mode.OFF);
            brewPot.updateConfig(config);
        }


        //Fermenter 

        if( session.getConfig().getMode() == SessionConfig.Mode.ACTIVE ) {

            switch( session.getConfig().getPhase()) {
                case FERMENTATION_START : {
                   //Placeholder for ferm
    
                }
               
                default: {
                    //Turn off
                }
            }

        } else {
            //Turn off
        }
        
      
    }




    
    
}
