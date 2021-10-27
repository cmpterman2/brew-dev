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
public class FermenterData {

    

    
    public FermenterData() {}
    
    public FermenterData(String heatGpio, String coolGpio, String probe, String airProbe, Config config, TemperatureReading lastReading, TemperatureReading airLastReading, Fermenter.State state, long scheduleStart) {
        this.heatGpio = heatGpio;
        this.coolGpio = coolGpio;
        this.probe = probe;
        this.airProbe = airProbe;
        this.config = config;
        this.lastReading = lastReading;
        this.airLastReading = airLastReading;
        this.state = state;
        this.scheduleStart = scheduleStart;
    }
    
    /**
     * @return the state
     */
    public Fermenter.State getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(Fermenter.State state) {
        this.state = state;
    }
    
    /**
     * @return the scheduleStart
     */
    public long getScheduleStart() {
        return scheduleStart;
    }

    /**
     * @param scheduleStart the scheduleStart to set
     */
    public void setScheduleStart(long scheduleStart) {
        this.scheduleStart = scheduleStart;
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
     * @return the heatGpio
     */
    public String getHeatGpio() {
        return heatGpio;
    }

    /**
     * @param heatGpio the heatGpio to set
     */
    public void setHeatGpio(String heatGpio) {
        this.heatGpio = heatGpio;
    }

    /**
     * @return the coolGpio
     */
    public String getCoolGpio() {
        return coolGpio;
    }

    /**
     * @param coolGpio the coolGpio to set
     */
    public void setCoolGpio(String coolGpio) {
        this.coolGpio = coolGpio;
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
     * @return the airProbe
     */
    public String getAirProbe() {
        return airProbe;
    }

    /**
     * @param airProbe the airProbe to set
     */
    public void setAirProbe(String airProbe) {
        this.airProbe = airProbe;
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

    /**
     * @return the airLastReading
     */
    public TemperatureReading getAirLastReading() {
        return airLastReading;
    }

    /**
     * @param airLastReading the airLastReading to set
     */
    public void setAirLastReading(TemperatureReading airLastReading) {
        this.airLastReading = airLastReading;
    }
    
    private Config config;
    private String heatGpio;
    private String coolGpio;
    
    private String probe;
    private String airProbe;

    private TemperatureReading lastReading;
    private TemperatureReading airLastReading;
    
    private Fermenter.State state;
    private long scheduleStart;
}
