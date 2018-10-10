/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.devices;

import com.brew.probes.TemperatureReading;

/**
 *
 * @author andrew.p.davis
 */
public class BurnerData {
    
    //Data - this is published / gettable
    private String gpio;
    private String probe;
    private Config config;
    private TemperatureReading lastReading;
    
    public BurnerData(){}
    
    public BurnerData(String gpio, String probe, Config config, TemperatureReading lastReading){
        this.gpio = gpio;
        this.probe = probe;
        this.config = config;
        this.lastReading = lastReading;
    }

    /**
     * @return the gpio
     */
    public String getGpio() {
        return gpio;
    }

    /**
     * @param gpio the gpio to set
     */
    public void setGpio(String gpio) {
        this.gpio = gpio;
    }

    /**
     * @return the probe
     */
    public String getProbe() {
        return probe;
    }

    /**
     * @param probe the probe to set
     */
    public void setProbe(String probe) {
        this.probe = probe;
    }

    /**
     * @return the config
     */
    public Config getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * @return the lastReading
     */
    public TemperatureReading getLastReading() {
        return lastReading;
    }

    /**
     * @param lastReading the lastReading to set
     */
    public void setLastReading(TemperatureReading lastReading) {
        this.lastReading = lastReading;
    }
}
