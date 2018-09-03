/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew;

import com.brew.gpio.Pin;
import com.brew.notify.Listener;
import com.brew.probes.OneWireDevices;
import com.brew.probes.OneWireMonitor;
import com.brew.probes.TemperatureReading;
import java.io.IOException;
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
    
    public static void main(String[] args) throws InterruptedException {
        LOG.info("Starting Main application");

        //Process Environment variables?
        //OneWireDevices.setDeviceDirectory(".\\src\\test\\resources\\probes\\");
        
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
        
        String pin = "GPIO1_12";
        int pinNumber = Pin.getPinNumber(pin);
        LOG.info("PIN {}:{}", pin, pinNumber);
        
        Pin.setupPin(pinNumber);
        Pin.writeValue(pinNumber, "1");
        try {
            Pin.readValue(pinNumber);
        } catch (RuntimeException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Thread.sleep(10000);
    }
    
}
