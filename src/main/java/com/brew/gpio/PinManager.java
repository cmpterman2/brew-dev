/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.gpio;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrew.p.davis
 */
public class PinManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(PinManager.class);
    
    private static final HashMap<String, Pin> pinMap = new HashMap();
    private static final Pattern pinPattern = Pattern.compile("(gpio)([0-9])_([0-9]+)$", Pattern.CASE_INSENSITIVE);

 
    public synchronized static Pin lookupPin(String gpio) {

        Pin pin = pinMap.get(gpio);
        if (pin == null) {
            int pinNumber = getPinNumber(gpio);
            if (pinNumber > 0) {
                pin = createPin(pinNumber);
                pinMap.put(gpio, pin);
            }
        }

        return pin;
    }

    private static Pin createPin(int pinNumber) {
        if( System.getProperty("mock") != null ) {
            return new MockPin();
        } 

        return new FilePin(pinNumber);
    }

    private static int getPinNumber(String pinName) {

        int pinNumber = -1;
        // See if we have the JSON for this Pin

        Matcher matcher = pinPattern.matcher(pinName);
        if (matcher.find()) {
            pinNumber = Integer.parseInt(matcher.group(3));
            pinNumber += Integer.parseInt(matcher.group(2)) * 32;
        } else {
            //throw new InvalidGPIOException("Could not match " + pinName + ". As a valid gpio pinout number");
        }

        return pinNumber;

    }

    

}
