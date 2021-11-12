/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.fermenter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author andrew.p.davis
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Entry {
    private float day;
    private float target;


    public Entry() {

    }

    public Entry(float day, float target) {
        this.day = day;
        this.target = target;
    }
    /**
     * @return the day
     */
    public float getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(float day) {
        this.day = day;
    }

    /**
     * @return the target
     */
    public float getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(float target) {
        this.target = target;
    }

    public boolean equals(Object o)
    {
        if( o instanceof Entry)
        {
            Entry entry = (Entry)o;
            return entry.getDay() == day && entry.getTarget() == target;
        }
        return false;
    }
}
