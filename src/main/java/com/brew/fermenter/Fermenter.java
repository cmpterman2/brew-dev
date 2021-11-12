/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.fermenter;

import com.brew.config.Configuration;
import com.brew.gpio.Pin;
import com.brew.gpio.PinManager;
import com.brew.notify.Event;
import com.brew.notify.Listener;
import com.brew.probes.OneWireMonitor;
import com.brew.probes.TemperatureReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrew.p.davis
 */
public class Fermenter {

    public static final String EVENT_FERM_STATE = "FERM.STATE";
    public static final String EVENT_FERM_CONFIG = "FERM.CONFIG";
    
    private static final Logger LOG = LoggerFactory.getLogger(Fermenter.class);
    
    private FermenterConfig config;
    private FermenterState state;
    private boolean changedState = false;

    private static final long COOLING_WAIT = 1000*60*5; //5 Minutes
    private static final long COOLING_MIN = 1000*60*5; //5 Minutes
    private static final long COOLING_MAX= 1000*60*10; //10 Minutes
    
    //private ScheduleThread scheduleThread;

    private static final long DAY = 1000*60*60*24;
    
    private static final float COOL_THRESHOLD = .5f;
    private static final float HEAT_THRESHOLD = -.1f;
    
    private long coolStart = 0;
    private long coolStop = 0;

    private float STEP = .25f;
    
    private Pin coolPin;
    private Pin heatPin;
    
    Thread pokeThread = new Thread((Runnable)() -> {
        try {
            Thread.sleep(30000);
            for(;;Thread.sleep(30000)) {
                update(null);
            }
        } catch (Exception e ) {}
    });
        
    
    
    
    public Fermenter() {
        this.coolPin = PinManager.lookupPin(Configuration.get().getCoolGPIO());
        this.heatPin = PinManager.lookupPin(Configuration.get().getHeatGPIO());
        
        //Listen for ferm and air changes
        OneWireMonitor.get().monitor(Configuration.get().getFermenterProbe(), (Listener<TemperatureReading>) (Event<TemperatureReading> event) -> {
            updateFermTemp(event.getData());
        });

        OneWireMonitor.get().monitor(Configuration.get().getAirProbe(), (Listener<TemperatureReading>) (Event<TemperatureReading> event) -> {
            updateAirTemp(event.getData());
        });

        config = new FermenterConfig();
        state = new FermenterState();
        state.setTarget(config.calculatePitchTarget());
        
        pokeThread.setDaemon(true);
        pokeThread.start();
    }



    public FermenterState getState() {
        return state.duplicate();
    }

    public FermenterConfig getConfig() {
        return config.duplicate();
    }
    
     //If null - just use current config, but re-process..
    public synchronized void update(FermenterConfig newConfig) {

        //Is this a configuration change?
        if( newConfig != null && !newConfig.compare(config) ) {
            LOG.info("Updating configuration: {}", newConfig);
            this.config = newConfig;

            //NOTIFY
            com.brew.notify.Notifier.notifyListeners(new Event<FermenterConfig> (EVENT_FERM_CONFIG, this.config.duplicate()));
        }

        if (this.config != null) {
            
            switch (config.getMode()) {
                case SCHEDULE: {

                    if( state.getScheduleStart() <= 0 ) {
                        updateScheduleStart(System.currentTimeMillis());
                    }

                    //Need to calculate target for right now.
                    float newTarget = calculateScheduleTarget();
                    updateTarget(newTarget);

                    //fall through to auto since it should go to auto
                }
                case TARGET_PITCH:
                    updateTarget(config.calculatePitchTarget());
                    
                    if (state.getFermTemp() != Float.NaN) {
                        float diff = state.getFermTemp() - state.getTarget();
                        if (diff > COOL_THRESHOLD) {
                            cool();
                        } else if (diff < HEAT_THRESHOLD){
                            heat();
                        } else {
                            middle();
                        }
                    } else {
                        off();
                    }
                    break;
                case OFF:
                    //Reset the schedule only on off
                    updateScheduleStart(-1l);
                    updateTarget(config.calculatePitchTarget());
                    //Fall through to next case
                default: //Handle OFF and for other unsupported cases.
                    off();
                    break;

            }
        }

        if( changedState ) {
            //State change..
            changedState = false;
            com.brew.notify.Notifier.notifyListeners(new Event<FermenterState>(EVENT_FERM_STATE, this.state.duplicate()));
            
        }
    }

    
    
    private void transition(FermenterState.DeviceState newState) {
        LOG.info("Fermenter transition: {} -> {}", state.getDeviceState(),newState);
        if( state.getDeviceState() == FermenterState.DeviceState.COOLING ) {
            coolStop =  System.currentTimeMillis();
        }
        
        switch (newState) {
            case COOLING_DELAY: 
                coolStart = 0;
                heatPin.turnOff();
                coolPin.turnOff();
                break;
            case COOLING: 
                coolStart = System.currentTimeMillis();
                heatPin.turnOff();
                coolPin.turnOn();
                break;
            case HEATING: 
                coolStart = 0;
                heatPin.turnOn();
                coolPin.turnOff();
                break;
            case OFF:
                coolStart = 0;
                heatPin.turnOff();
                coolPin.turnOff();
                break;
        }

        updateState(newState);
        
    }
    
    private void cool() {
        switch (state.getDeviceState()) {
            case COOLING_DELAY: //Eventually transition to cooling
                //Check to see if time has passed so we can transition to cooling.
                if( System.currentTimeMillis() - coolStop > COOLING_WAIT ) {
                    transition(FermenterState.DeviceState.COOLING);
                }
                break;
            case COOLING: //Make sure we don't run for too long
                if( System.currentTimeMillis() - coolStart > COOLING_MAX) {
                    transition(FermenterState.DeviceState.COOLING_DELAY);
                }
                break;
            case HEATING: //Turn the cooling on!
            case OFF: {
                //Check for delay! --> COOLING_DELAY
                if( System.currentTimeMillis() - coolStop < COOLING_WAIT ) {
                    transition(FermenterState.DeviceState.COOLING_DELAY);
                } else {
                    transition(FermenterState.DeviceState.COOLING);
                }
                break;
            }
        }
    }
    
    private void heat() {
        switch (state.getDeviceState()) {
            case OFF: 
            case COOLING_DELAY: //Eventually transition to cooling
                transition(FermenterState.DeviceState.HEATING);
                break;
            case COOLING: 
                //Switch to heating (maybe)...
                if( System.currentTimeMillis() - coolStart > COOLING_MIN) {
                    transition(FermenterState.DeviceState.HEATING);
   
                } else {
                    //TODO - don't turn off cooling too soon ... unless target temp has changed..
                    //transition(FermenterState.State.HEATING);
                }
                
                
                break;
        }
    }
    
    private void off() {
        //Transition anyways?
        if( state.getDeviceState() != FermenterState.DeviceState.OFF ) {
            transition(FermenterState.DeviceState.OFF);
        }
    }
    
    private void middle() {
        switch (state.getDeviceState()) {
            case HEATING: 
            case COOLING_DELAY: //Eventually transition to cooling
                transition(FermenterState.DeviceState.OFF);
                break;
            case COOLING: 
                //don't turn off cooling too soon
                if( System.currentTimeMillis() - coolStart > COOLING_MIN) {
                    transition(FermenterState.DeviceState.OFF);
                } 
                break;
            case OFF: {
                //Nothin
                break;
            }
        }
    }
    
    void updateAirTemp(TemperatureReading notification){
        if( state.getAirTemp() == Float.NaN || state.getAirTemp() != notification.calculateTempInF() ){
            state.setAirTemp(notification.calculateTempInF());
            changedState = true;
        }
    }

    void updateFermTemp(TemperatureReading notification) {
        if( state.getFermTemp() == Float.NaN || state.getFermTemp() != notification.calculateTempInF() ){
            state.setFermTemp(notification.calculateTempInF());
            changedState = true;
        }
    }

    private void updateTarget(float target) {
        if (state.getTarget() != target ){
            changedState = true;
            state.setTarget(target);
        }
    }

    private void updateState(FermenterState.DeviceState newState) {
        if (this.state.getDeviceState() != newState ){
            changedState = true;
            state.setDeviceState(newState);
        }
    }

    private void updateScheduleStart(long scheduleStart) {
        if( this.state.getScheduleStart() != scheduleStart ){
            changedState = true;
            state.setScheduleStart(scheduleStart);
        }
    }
    
        
   private float calculateScheduleTarget()
   {
       float days = (float) (System.currentTimeMillis() - this.state.getScheduleStart()) / DAY;

       Entry priorEntry = null;
       Entry currentEntry = null;
       Entry nextEntry = null;
       boolean slope = false;

       for (Entry entry :  config.getSchedule()) {
           if( days >= entry.getDay() ) {
               priorEntry = currentEntry;
               currentEntry = entry;
           } else if ( nextEntry == null ){
                nextEntry = entry;
           }
       }

    //    if (currentEntry == null ) {
    //        return config.getTarget();
    //    }

       //If prior two points have same target, slope dat shit
       if( priorEntry != null && currentEntry != null ) {
        slope = priorEntry.getTarget() == currentEntry.getTarget();
        }   

       if( slope && nextEntry!=null ) {
           float proportion = (days - currentEntry.getDay()) / (nextEntry.getDay() - currentEntry.getDay());
           float tempDiff = proportion * (nextEntry.getTarget()-currentEntry.getTarget());
           float roundTempDiff = (float) Math.round(tempDiff / STEP) * STEP;

           return currentEntry.getTarget()+roundTempDiff;
       } else {
           return currentEntry.getTarget();
       }

   }
    
}
