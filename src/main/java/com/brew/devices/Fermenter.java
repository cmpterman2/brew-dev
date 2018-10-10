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

/**
 *
 * @author andrew.p.davis
 */
public class Fermenter implements Listener<TemperatureReading> {
    
    private Config config;
    private String heatGpio;
    private String coolGpio;
    
    private String probe;
    private String airProbe;

    private TemperatureReading lastReading;
    private TemperatureReading airLastReading;
    
    
    private Pin coolPin;
    private Pin heatPin;
    
    
    public Fermenter() {
        this.heatGpio = Configuration.get().getHeatGPIO();
        this.coolGpio = Configuration.get().getCoolGPIO();
        this.probe = Configuration.get().getFermenterProbe();
        this.airProbe = Configuration.get().getAirProbe();
        
        this.coolPin = Pin.lookupPin(coolGpio);
        this.heatPin = Pin.lookupPin(heatGpio);
        
        //Need different listeners ugh..
        OneWireMonitor.get().monitor(probe, this);
        OneWireMonitor.get().monitor(airProbe, new Listener<TemperatureReading>() {
            @Override
            public void notify(TemperatureReading notification) {
                notifyAir(notification);
            }
        
        });
        
        
        
        config = new Config(0,0,Mode.OFF);
    }
    
    public void notifyAir(TemperatureReading notification){
        this.airLastReading = notification;
    }

    @Override
    public void notify(TemperatureReading notification) {
        this.lastReading = notification;
    }
    
    public FermenterData getFermenterData() {
        return new FermenterData(heatGpio, coolGpio, probe, airProbe, config, lastReading, airLastReading);
    }
    
   
    
}
