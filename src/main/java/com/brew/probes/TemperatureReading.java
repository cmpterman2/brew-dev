/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.probes;

import java.math.BigDecimal;
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
        return df.format(calculateTempInF());
    }

    public float getTempInC() {
        return this.tempInC;
    }
    
    public float calculateTempInF() {
        return calculateTempInF(1);
    }

    public float calculateTempInF(int decimalPlace) {
        return round(calculateFullTempInF(), decimalPlace); 
    }

    public float calculateFullTempInF() {
        return this.tempInC*9.0f/5.0f + 32.0f;
    }
    
    public long getReadTime() {
        return readTime;
    }

    public String getProbe() {
        return probe;
    }
    
    public String toString() {
        return "Temp ("+probe+"): "+getDisplayTemp()+"F";
    }


    public static float round(float value, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++) {
            pow *= 10;
        }
        float tmp = value * pow;
        float tmpSub = tmp - (int) tmp;
    
        return ( (float) ( (int) (
                value >= 0
                ? (tmpSub >= 0.5f ? tmp + 1 : tmp)
                : (tmpSub >= -0.5f ? tmp : tmp - 1)
                ) ) ) / pow;
    
        // Below will only handles +ve values
        // return ( (float) ( (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) ) ) / pow;
    }
    
}
