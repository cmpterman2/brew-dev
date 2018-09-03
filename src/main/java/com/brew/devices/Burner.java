/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.devices;

/**
 *
 * @author andrew.p.davis
 */
public class Burner {
    
    String gpio;
    String probe;
    
    enum Mode {
        
    }
    
    //Do we want a config for all devices?
    public class Config {
        int duty;
        float target;
        Mode mode;
    }
    
    //Ferment has a schedule option..
    
    
    //duty
    //target
    
    
   
    //Mode
    //ON
    //OFF
    //AUTO - Target Temp
    //DUTY Percent
    
    //Need to be able to change the mode and have this respond immediately...
    
    //Writes might be slow?
    
    public void go() {
        Mode.valueOf("FUCK");
    }
    
    public void update(Config config) {
        
    }
    
    public Config getConfig() {
        return null;
    }
}
