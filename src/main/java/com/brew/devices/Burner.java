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
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrew.p.davis
 */
public class Burner implements Runnable, Listener<TemperatureReading> {

    private static final Logger LOG = LoggerFactory.getLogger(Burner.class);

    //Data - this is published / gettable
    private String gpio;
    private String probe;
    private Config config;
    private TemperatureReading lastReading;
    
    //Internal data
    private Pin pin;
    private boolean pinOn = false;
    int minRunTime = 100; //ms of run time.
    private Thread autoThread;

    private void turnOn(){
        if( !pinOn){
            pin.turnOn();
            pinOn = true;
        }
    }

    private void turnOff(){
        if ( pinOn ){
            pin.turnOff();
            pinOn = false;
        }
    }

    @Override
    public void notify(TemperatureReading notification) {
        lastReading = notification;
        //Call update to process configuration
        //Call with null to avoid timing issues
        this.update(null);
    }

    public Burner() {
        this.gpio = Configuration.get().getBurnerGPIO();
        this.probe = Configuration.get().getBurnerProbe();
        this.pin = Pin.lookupPin(gpio);
        //Register thermometer too..
        OneWireMonitor.get().monitor(probe, this);
        config = new Config(0,0,Mode.OFF);
    }

    //Ferment has a schedule option..
    //duty
    //target
    //Mode
    //ON
    //OFF
    //AUTO - Target Temp
    //DUTY Percent
    //Need to be able to change the mode and have this respond immediately...
    //Writes might be slow?
    //Duty thread...
    public void run() {
        //Do we thread this?
        //Need to loop?
        boolean running = true;

        if( config.getDuty() <=0 || config.getDuty() >= 100 ){
            running = false;
            LOG.error("Invalid Duty {}", config.getDuty());
        }
        try{
            while (running) {

                //If auto - need to be based on temp... hmm
                //Start thread in duty mode
                long start = System.currentTimeMillis();
                //Do smaller first...

                if (config.getDuty() > 50) {
                    turnOff();
                } else {
                    turnOn();
                }
                Thread.sleep(this.minRunTime);

                
                long stop = System.currentTimeMillis();
                long timeTaken = stop - start;

                LOG.debug("First action took: {} ms.", timeTaken);

                timeTaken = timeTaken < 1 ? 1 : timeTaken;
                long timeNeeded = 0;
                if( config.getDuty() > 50 ) {
                    timeNeeded = (timeTaken * config.getDuty() / (100 - config.getDuty()) );
                } else {
                    timeNeeded = (timeTaken * (100 - config.getDuty()) / config.getDuty());
                }
                
                start = System.currentTimeMillis();

                   
                

                if (config.getDuty() > 50) {
                    turnOn();
                } else {
                    turnOff();
                }
                Thread.sleep(timeNeeded);
                stop = System.currentTimeMillis();
                LOG.debug("Second action took: {} ms.", stop - start);
            }
        } catch (InterruptedException ex) {
            LOG.debug("Interrupted Duty Thread");
        }
    }

    //If null - just use current config, but re-process..
    public synchronized void update(Config newConfig) {
        //Compare to existing config?
        
        if( newConfig != null ) {
            LOG.debug("Updating configuration: {}", this.config);
            this.config = newConfig;
        }

        if (this.config != null) {

            if (autoThread != null) {
                autoThread.interrupt();
                //autoThread.
                turnOff();
            }

            
            switch (config.getMode()) {
                case ON:
                    turnOn();
                    break;
                case OFF:
                    turnOff();
                    break;
                case AUTO:
                    if (this.lastReading != null) {
                        if (lastReading.calculateTempInF() > config.getTarget()) {
                            turnOff();
                        } else {
                            turnOn();
                        }
                    } else {
                        turnOff();
                    }
                    break;
                case DUTY:
                    autoThread = new Thread(this);
                    autoThread.start();
                    break;
                default:
                    turnOff();
                    break;

            }
        }
    }

    public Config getConfig() {
        return config;
    }
    
    public BurnerData getBurnerData() {
        return new BurnerData(gpio, probe, config, lastReading);
    }
   
}
