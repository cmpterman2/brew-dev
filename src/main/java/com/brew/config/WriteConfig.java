/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.config;

/**
 *
 * @author andrew.p.davis
 */
public class WriteConfig {
    
    public static void main(String[] args) {
        Configuration.create();
        Configuration config = Configuration.get();
        config.setBurnerGPIO("GPIO1_12");
        config.setBurnerProbe("28-12345");
        Configuration.save();
        
        
    }
}
