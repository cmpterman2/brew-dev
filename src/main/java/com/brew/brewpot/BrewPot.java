package com.brew.brewpot;

import com.brew.brewpot.BrewPotConfig.Mode;
import com.brew.config.Configuration;
import com.brew.gpio.Pin;
import com.brew.gpio.PinManager;
import com.brew.notify.Event;
import com.brew.notify.Listener;
import com.brew.probes.OneWireMonitor;
import com.brew.probes.TemperatureReading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrewPot implements Runnable {

    public static final String EVENT_BREW_STATE = "BREW.STATE";
    public static final String EVENT_BREW_CONFIG = "BREW.CONFIG";

    private static final Logger LOG = LoggerFactory.getLogger(BrewPot.class);
    private Pin heatPin;
    private BrewPotState state;
    private BrewPotConfig config;
    private int minRunTime = 100; //ms of run time.
    private int longSleepTime = 100000; 
    private boolean tooHot = false;

    private Thread deviceThread;

    boolean changedState = false;


    public BrewPot() {

        this.heatPin = PinManager.lookupPin(Configuration.get().getBurnerGPIO());
        
        //Listen for ferm and air changes
        OneWireMonitor.get().monitor(Configuration.get().getBurnerProbe(), (Listener<TemperatureReading>) (Event<TemperatureReading> event) -> {
            updateTemp(event.getData());
        });

        state = new BrewPotState();
        config = new BrewPotConfig(212.0f, Mode.OFF);

        deviceThread = new Thread(this);
        deviceThread.setDaemon(true);
        deviceThread.start();

        

    }

    void updateTemp(TemperatureReading notification){
        if( state.getTemp() == Float.NaN || state.getTemp() != notification.calculateTempInF() ){
             state.setTemp(notification.calculateTempInF());
             changedState = true;
        }

        //Check if temp is > limits and check to see if we are in a condition to notify -- IE, don't keep notifying all the time
        checkTemp();

        //For now we can notify state here..
        com.brew.notify.Notifier.notifyListeners(new Event<BrewPotState> (EVENT_BREW_STATE, this.state.duplicate()));
    }

    

    public synchronized void updateConfig(BrewPotConfig newConfig) {
        //Is this a configuration change?
        if( newConfig != null && !newConfig.compare(config) ) {
            LOG.info("Updating configuration: {}", newConfig);
            this.config = newConfig;

            //Process change
            
            deviceThread.interrupt();


            //NOTIFY
            com.brew.notify.Notifier.notifyListeners(new Event<BrewPotConfig> (EVENT_BREW_CONFIG, this.config.duplicate()));



        }

    }

    public BrewPotConfig getConfig() {
        return config.duplicate();
    }

    private void checkTemp() {
        if( !tooHot && state.getTemp() >= config.getTarget() || tooHot && state.getTemp() < config.getTarget() ) {
            deviceThread.interrupt();
        }
    }




    @Override
    public void run() {
        boolean running = true;
        while (running) {

        try {
            
            Mode mode = config.getMode();
            if( state.getTemp() >= config.getTarget() ) {
                tooHot = true;
                mode = Mode.OFF;
            } else {
                tooHot = false;
            }

            
            int duty = 50; //MED
            //Process current config...
            switch (mode) {

                case MED:
                case LOW: 
                    duty = (mode == Mode.LOW ? 25 : 50);

                    long start = System.currentTimeMillis();
                    //Do smaller first...

                    if (duty > 50) {
                        heatPin.turnOff();
                    } else {
                        heatPin.turnOn();
                    }

                    Thread.sleep(this.minRunTime);

                    long stop = System.currentTimeMillis();
                    long timeTaken = stop - start;

                    LOG.debug("First action took: {} ms.", timeTaken);

                    timeTaken = timeTaken < 1 ? 1 : timeTaken;
                    long timeNeeded = 0;
                    if( duty > 50 ) {
                        timeNeeded = (timeTaken * duty / (100 - duty) );
                    } else {
                        timeNeeded = (timeTaken * (100 - duty) / duty);
                    }
                    
                    start = System.currentTimeMillis();

                    
                    

                    if (duty > 50) {
                        heatPin.turnOn();
                    } else {
                        heatPin.turnOff();
                    }
                    Thread.sleep(timeNeeded);
                    stop = System.currentTimeMillis();
                    LOG.debug("Second action took: {} ms.", stop - start);


                    break;
                case HIGH:
                    heatPin.turnOn();
                    Thread.sleep(this.longSleepTime);
                    break;
                case OFF:
                default:
                    heatPin.turnOff();
                    Thread.sleep(this.longSleepTime);
                    break;

            }

          
            
        } catch (InterruptedException ex) {
            LOG.debug("Interrupted Burner Thread");
        }
    }
        
    }

    public BrewPotState getState() {
        return state.duplicate();
    }



    
}
