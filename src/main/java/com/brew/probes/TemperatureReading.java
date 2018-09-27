/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.probes;

import java.text.DecimalFormat;

/**
 *
 * @author andrew.p.davis
 */
public class TemperatureReading {

    private long readTime;
    private float tempInC;
    private String probe;
    //Not threadsafe... just hope for the best... ?
    private DecimalFormat df = new DecimalFormat("###.##");
    
    public TemperatureReading(String probe, long readTime, float tempInC) {
        this.readTime = readTime;
        this.tempInC = tempInC;
        this.probe = probe;
    }
    
    public synchronized String getDisplayTemp(){
        return df.format(getTempInF());
    }

    protected float getTempInC() {
        return this.tempInC;
    }
    
    public float getTempInF() {
        return this.tempInC*9.0f/5.0f + 32.0f;
    }
    
    public long getReadTime() {
        return readTime;
    }
    
    public String toString() {
        return "Temp ("+probe+"): "+getDisplayTemp()+"F";
    }
    
}
