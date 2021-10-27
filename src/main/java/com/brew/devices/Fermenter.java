/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.devices;

import com.brew.config.Configuration;
import com.brew.gpio.Pin;
import com.brew.notify.Listener;
import com.brew.probes.OneWireMonitor;
import com.brew.probes.TemperatureReading;
import java.text.NumberFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

/**
 *
 * @author andrew.p.davis
 */
public class Fermenter implements Listener<TemperatureReading> {
    
    private static final Logger LOG = LoggerFactory.getLogger(Fermenter.class);
    
    private Config config;
    private String heatGpio;
    private String coolGpio;
    
    private String probe;
    private String airProbe;
    
    private State state = State.OFF;
    private static final long COOLING_WAIT = 1000*60*5; //5 Minutes
    private static final long COOLING_MIN = 1000*60*5; //5 Minutes
    private static final long COOLING_MAX= 1000*60*10; //10 Minutes
    
    //private ScheduleThread scheduleThread;

    private static final long DAY = 1000*60*60*24;
    private long scheduleStart = -1L;
    

    enum State {
        COOLING, HEATING, DELAY_COOLING, OFF
    }

    private TemperatureReading lastReading;
    private TemperatureReading airLastReading;

    private ArrayList<Listener> targetListeners = new ArrayList();
    private ArrayList<Listener> stateListeners = new ArrayList();
    
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
                update(null, null);
            }
        } catch (Exception e ) {}
    });
        
    
    
    
    public Fermenter() {
        this.heatGpio = Configuration.get().getHeatGPIO();
        this.coolGpio = Configuration.get().getCoolGPIO();
        this.probe = Configuration.get().getFermenterProbe();
        this.airProbe = Configuration.get().getAirProbe();
        
        this.coolPin = Pin.lookupPin(coolGpio);
        this.heatPin = Pin.lookupPin(heatGpio);
        
        //Need different listeners ugh..
        OneWireMonitor.get().monitor(probe, this);
        OneWireMonitor.get().monitor(airProbe, (Listener<TemperatureReading>) (TemperatureReading notification) -> {
            notifyAir(notification);
        });
        
        pokeThread.setDaemon(true);
        pokeThread.start();
        
        
        
        config = new Config(0,0,Mode.OFF);
    }

    public void addTargetListener(Listener listener) {
        if (!targetListeners.contains(listener)) {
            targetListeners.add(listener);
        }
    }

    public void addStateListener(Listener listener) {
        if (!stateListeners.contains(listener)) {
            stateListeners.add(listener);
        }
    }
    
    
     //If null - just use current config, but re-process..
    public synchronized void update(Config newConfig, String source) {

        State oldState = this.state;
        float oldTarget = this.config.getTarget();

        //Is this a configuration change?
        if( newConfig != null ) {
            //Validate source is schedule thread to make sure it only changes the target and doesn't overwrite something else.
            if( Mode.SCHEDULE.toString().equals(source) && ! config.compare(newConfig) ) {
                LOG.warn("Avoided configuration overwrite from schedule thread");
            }
            else {
                LOG.info("Updating configuration: {}", this.config);
                this.config = newConfig;
            }
        }

        


        if (this.config != null) {
            
            switch (config.getMode()) {
                case SCHEDULE: {

                    if( scheduleStart <= 0 ) {
                        scheduleStart = System.currentTimeMillis();
                    }

                    //Need to calculate target for right now.
                    //config.setTarget(0f);
                    float newTarget = calculateScheduleTarget();
                    //Start thread
                    config.setTarget(newTarget);



                    //fall through to auto since it should go to auto
                }
                case AUTO:
                    if (this.lastReading != null) {
                        float diff = lastReading.calculateTempInF() - config.getTarget();
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
                default: //Handle OFF and for other unsupported cases.
                    scheduleStart = -1l;
                    off();
                    break;

            }
        }

        if( this.state != oldState) {
            com.brew.notify.Notifier.notifyListeners(stateListeners, this.state);
        }

        if( this.config.getTarget() != oldTarget ) {
            com.brew.notify.Notifier.notifyListeners(targetListeners, this.config.getTarget());
        }
    }
    
    private void transition(State prior, State newState) {
        LOG.info("Fermenter transition: {} -> {}", prior,newState);
        if( prior == State.COOLING ) {
            coolStop =  System.currentTimeMillis();
        }
        
        switch (newState) {
            case DELAY_COOLING: 
                state = State.DELAY_COOLING;
                coolStart = 0;
                heatPin.turnOff();
                coolPin.turnOff();
                break;
            case COOLING: 
                state = State.COOLING;
                coolStart = System.currentTimeMillis();
                heatPin.turnOff();
                coolPin.turnOn();
                break;
            case HEATING: 
                state = State.HEATING;
                coolStart = 0;
                heatPin.turnOn();
                coolPin.turnOff();
                break;
            case OFF:
                state = State.OFF;
                coolStart = 0;
                heatPin.turnOff();
                coolPin.turnOff();
                break;
        }
    }
    
    private void cool() {
        switch (state) {
            case DELAY_COOLING: //Eventually transition to cooling
                //Check to see if time has passed so we can transition to cooling.
                if( System.currentTimeMillis() - coolStop > COOLING_WAIT ) {
                    transition(state, State.COOLING);
                }
                break;
            case COOLING: //Make sure we don't run for too long
                if( System.currentTimeMillis() - coolStart > COOLING_MAX) {
                    transition(state, State.DELAY_COOLING);
                }
                break;
            case HEATING: //Turn the cooling on!
            case OFF: {
                //Check for delay! --> DELAY_COOLING
                if( System.currentTimeMillis() - coolStop < COOLING_WAIT ) {
                    transition(state, State.DELAY_COOLING);
                } else {
                    transition(state, State.COOLING);
                }
                break;
            }
        }
    }
    
    private void heat() {
        switch (state) {
            case OFF: 
            case DELAY_COOLING: //Eventually transition to cooling
                transition(state, State.HEATING);
                break;
            case COOLING: 
                //Switch to heating (maybe)...
                if( System.currentTimeMillis() - coolStart > COOLING_MIN) {
                    transition(state, State.HEATING);
   
                } else {
                    //TODO - don't turn off cooling too soon ... unless target temp has changed..
                    //transition(state, State.HEATING);
                }
                
                
                break;
        }
        if( state != State.HEATING ) {
            
        }
    }
    
    private void off() {
        //Transition anyways?
        if( state != State.OFF ) {
            transition(state, State.OFF);
        }
    }
    
    private void middle() {
        switch (state) {
            case HEATING: 
            case DELAY_COOLING: //Eventually transition to cooling
                transition(state, State.OFF);
                break;
            case COOLING: 
                //don't turn off cooling too soon
                if( System.currentTimeMillis() - coolStart > COOLING_MIN) {
                    transition(state, State.OFF);
                } 
                break;
            case OFF: {
                //Nothin
                break;
            }
        }
    }
    
    public void notifyAir(TemperatureReading notification){
        this.airLastReading = notification;
    }

    @Override
    public void notify(TemperatureReading notification) {
        this.lastReading = notification;
    }
    
    public FermenterData getFermenterData() {
        return new FermenterData(heatGpio, coolGpio, probe, airProbe, config, lastReading, airLastReading, state, scheduleStart);
    }
    
   public float calculateScheduleTarget()
   {
       float days = (float) (System.currentTimeMillis() - this.scheduleStart) / DAY;

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

       if (currentEntry == null ) {
           return config.getTarget();
       }

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
