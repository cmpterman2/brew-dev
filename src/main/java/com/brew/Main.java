/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew;

import com.brew.config.Configuration;
import com.brew.devices.Burner;
import com.brew.devices.Config;
import com.brew.devices.Mode;
import com.brew.gpio.Pin;
import com.brew.notify.Listener;
import com.brew.probes.OneWireDevices;
import com.brew.probes.OneWireMonitor;
import com.brew.probes.TemperatureReading;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrew.p.davis
 */
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        LOG.info("Starting Main application");
        
        Configuration.load();
        
        //If burner configured, create it..?
        Burner burner = null;
        if( Configuration.get().getBurnerGPIO()!=null && Configuration.get().getBurnerProbe()!=null) {
            LOG.info("Creating Burner");
            burner = new Burner();
        }
        
        //If ferm configured, create it too?
        
        List<String> probes = OneWireDevices.listOneWireProbes();
        
        LOG.info("Found temperature probes: {}", probes);
        for (String probe : probes) {
            OneWireMonitor.get().monitor(probe, new Listener<TemperatureReading>(){
                @Override
                public void notify(TemperatureReading notification) {
                    LOG.info("Received Notification: {}", notification);
                }
            });
        }

        
        //burner.update(new Config(0, 100.0f, Mode.AUTO));
        
//        String pinName = "GPIO1_14";
//        Pin pin = Pin.lookupPin(pinName);
//        LOG.info("PIN {}:{}", pinName, pin);
//        long start = System.currentTimeMillis();
//        pin.turnOn();
//        long stop = System.currentTimeMillis();
//        long difference = stop-start;
//        LOG.info("ON took {} ms.", difference);
//        
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException ex) {
//            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        start = System.currentTimeMillis();
//        pin.turnOff();
//        stop = System.currentTimeMillis();
//        difference = stop-start;
//        LOG.info("OFF took {} ms.", difference);
        
        
        //Only taking 4-6 ms to write out... hmm
        //Now updated, may be faster as it doesn't do a read...
        //pin.turnOff();
    }
    
}
