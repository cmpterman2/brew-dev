package com.brew.brewpot;

public class BrewPotState {

    private float temp = Float.NaN;

    

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public BrewPotState duplicate() {
        BrewPotState newConfig = new BrewPotState();
        newConfig.setTemp(temp);

        return newConfig;
    }

    public String toString() {
        return "Temp: "+temp;
    }


}
