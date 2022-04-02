package com.brew.ispindel;

import com.fasterxml.jackson.annotation.JsonSetter;

public class ISpindelData {
    private String name;
    private int id;
    private float angle;
    private float temperature;
    private String tempUnits;
    private float battery;
    private float gravity;
    private int interval;
    private float rssi;

    public String getName() {
        return name;
    }
    public float getRssi() {
        return rssi;
    }

    @JsonSetter("RSSI")
    public void setRssi(float rssi) {
        this.rssi = rssi;
    }
    public int getInterval() {
        return interval;
    }
    public void setInterval(int interval) {
        this.interval = interval;
    }
    public float getGravity() {
        return gravity;
    }
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
    public float getBattery() {
        return battery;
    }
    public void setBattery(float battery) {
        this.battery = battery;
    }
    public String getTempUnits() {
        return tempUnits;
    }
    @JsonSetter("temp_units")
    public void setTempUnits(String tempUnits) {
        this.tempUnits = tempUnits;
    }
    public float getTemperature() {
        return temperature;
    }
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
    public float getAngle() {
        return angle;
    }
    public void setAngle(float angle) {
        this.angle = angle;
    }
    public int getId() {
        return id;
    }
    @JsonSetter("ID")
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "name:"+name+" ID:"+id+" angle:"+angle+" temperature:"+temperature+" temp_units:"+tempUnits+" battery:"+battery+" gravity:"+gravity+" interval:"+interval+" rssi:"+rssi;
    }
}