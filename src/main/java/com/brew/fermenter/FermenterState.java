/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.fermenter;

/**
 *
 * @author andrew.p.davis
 */
public class FermenterState implements Cloneable {

    private float target;
    private float fermTemp = Float.NaN;
    private float airTemp = Float.NaN;
    private DeviceState deviceState = DeviceState.OFF;
    private long scheduleStart = -1l;

    

    public enum DeviceState {
        COOLING, HEATING, COOLING_DELAY, OFF
    }


    
    public FermenterState() {
    }

    public long getScheduleStart() {
        return scheduleStart;
    }

    public void setScheduleStart(long scheduleStart) {
        this.scheduleStart = scheduleStart;
    }

    public FermenterState(float fermTemp, float airTemp, DeviceState deviceState, float target, long scheduleStart) {

        this.fermTemp = fermTemp;
        this.airTemp = airTemp;
        this.deviceState = deviceState;
        this.target = target;
        this.scheduleStart = scheduleStart;
        
     }
    
    public DeviceState getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(DeviceState state) {
        this.deviceState = state;
    }

    public float getAirTemp() {
        return airTemp;
    }

    public void setAirTemp(float airTemp) {
        this.airTemp = airTemp;
    }

    public float getFermTemp() {
        return fermTemp;
    }

    public void setFermTemp(float fermTemp) {
        this.fermTemp = fermTemp;
    }

    public float getTarget() {
        return target;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public FermenterState duplicate() {
        return new FermenterState( fermTemp, airTemp, deviceState, target, scheduleStart);
    }

    
   
}
