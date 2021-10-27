/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.devices;

import java.util.ArrayList;

/**
 *
 * @author andrew.p.davis
 */
public class Config {
    
    private int duty;
    private float target;
    private Mode mode;
    private ArrayList<Entry> schedule = new ArrayList();

    public Config(int duty, float target, Mode mode) {
        this.duty = duty;
        this.target = target;
        this.mode = mode;
    }
    
    public Config() {
        
    }

    /**
     * @return the duty
     */
    public int getDuty() {
        return duty;
    }

    /**
     * @param duty the duty to set
     */
    public void setDuty(int duty) {
        this.duty = duty;
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

    /**
     * @return the mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * @return the schedule
     */
    public ArrayList<Entry> getSchedule() {
        return schedule;
    }

    /**
     * @param schedule the schedule to set
     */
    public void setSchedule(ArrayList<Entry> schedule) {
        if( schedule == null ) {
            schedule = new ArrayList();
        }
        this.schedule = schedule;
    }

    public boolean compare(Config config) {
        boolean same = true;
        same = same && duty == config.getDuty();
        same = same && mode == config.getMode();
        same = same && schedule.size() == config.getSchedule().size();
        if( same )
        {
            for(int x=0; x<schedule.size(); x++)
            {
                same = same && schedule.get(x).equals(config.getSchedule().get(x));
            }
        }

        return same;
    }
    
    
}
