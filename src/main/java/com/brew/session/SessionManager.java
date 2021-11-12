/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.session;

import com.brew.fermenter.Fermenter;
import com.brew.fermenter.FermenterConfig;
import com.brew.fermenter.FermenterState;
import com.brew.notify.Event;
import com.brew.notify.Listener;
import com.brew.notify.Notifier;

/**
 *
 * @author andrew.p.davis
 */
public class SessionManager {
    
    private Session session;



    public SessionManager() {
        //Temporary
        

    }

    public void startNewSession() {
        session = new Session();
        session.setBrewSession(new BrewSession());

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



    
    
}
