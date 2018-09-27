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

    String gpio;
    String probe;
    int minRunTime = 100; //ms of run time.
    Config config;
    Pin pin;
    Thread autoThread;
    TemperatureReading lastReading;

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
        while (running) {

            //If auto - need to be based on temp... hmm
            //Start thread in duty mode
            long start = System.currentTimeMillis();
            //Do smaller first...

            if (config.getDuty() > 50) {
                pin.turnOff();
            } else {
                pin.turnOn();
            }
            long stop = System.currentTimeMillis();
            long timeTaken = stop - start;

            LOG.debug("First action took: {} ms.", timeTaken);

            timeTaken = timeTaken < 1 ? 1 : timeTaken;

            long timeNeeded = (timeTaken * (100 - config.getDuty()) / config.getDuty()) - timeTaken;
            LOG.debug("First action took: {} ms.", timeTaken);

            try {
                Thread.sleep(timeNeeded);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(Burner.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (config.getDuty() > 50) {
                pin.turnOn();
            } else {
                pin.turnOff();
            }

        }

    }

    //If null - just use current config, but re-process..
    public synchronized void update(Config newConfig) {
        //Compare to existing config?
        
        if( newConfig != null ) {
            this.config = newConfig;
        }

        if (this.config != null) {

            if (autoThread != null) {
                autoThread.interrupt();
                //autoThread.
            }

            LOG.debug("Updating configuration: {}", this.config.getMode());
            switch (config.getMode()) {
                case ON:
                    pin.turnOn();
                    break;
                case OFF:
                    pin.turnOff();
                    break;
                case AUTO:
                    if (this.lastReading != null) {
                        if (lastReading.getTempInF() > config.getTarget()) {
                            pin.turnOff();
                        } else {
                            pin.turnOn();
                        }
                    } else {
                        pin.turnOff();
                    }
                    break;
                case DUTY:
                    autoThread = new Thread(this);
                    autoThread.start();
                    break;
                default:
                    pin.turnOff();
                    break;

            }
        }
    }

    public Config getConfig() {
        return config;
    }

}
