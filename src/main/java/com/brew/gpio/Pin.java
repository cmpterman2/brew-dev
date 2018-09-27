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
public class Pin {
    
     private static final Logger LOG = LoggerFactory.getLogger(Pin.class);

    private static final String BASE_PATH = "/sys/class/gpio";
    private static final String EXPORT_PATH = BASE_PATH + "/export";
    private static final String UNEXPORT_PATH = BASE_PATH + "/unexport";
    private static final String DEVICE_PATH = BASE_PATH + "/gpio%d";
    private static final String DIRECTION_PATH = DEVICE_PATH + "/direction";
    private static final String VALUE_PATH = DEVICE_PATH + "/value";
    private static final String ACTIVELOW_PATH = DEVICE_PATH + "/active_low";
    private static final String BASE_DEVICES = "/sys/devices/";

    private static final HashMap<String, Pin> pinMap = new HashMap();
    private static final Pattern pinPattern = Pattern.compile("(gpio)([0-9])_([0-9]+)$", Pattern.CASE_INSENSITIVE);

    private int number;
    private String valuePath;

    public void turnOn() {
        LOG.debug("ON: {}", number);
        writeValue("1");
    }

    public void turnOff() {
        LOG.debug("OFF: {}", number);
        writeValue("0");
    }
    
    public String toString() {
        return "Pin Number: "+number;
    }

    public synchronized static Pin lookupPin(String gpio) {

        Pin pin = pinMap.get(gpio);
        if (pin == null) {
            int pinNumber = getPinNumber(gpio);
            if (pinNumber > 0) {
                pin = new Pin(pinNumber);
                pin.setup();
                pinMap.put(gpio, pin);
            }
        }

        return pin;
    }

    private Pin(int number) {
        this.number = number;
        this.valuePath = String.format(VALUE_PATH, number);
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

    private static void writeFile(String fileName, String value) throws RuntimeException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(value.getBytes());
        } catch (FileNotFoundException | SecurityException fnfe) {
            throw new RuntimeException("Permission denied to GPIO file: " + fnfe.getMessage());
        } catch (IOException e) {
            System.out.println("GPIO is already exported, continuing");
        }
    }

    private static String readFile(String filename) throws FileNotFoundException, RuntimeException, IOException {
        try (BufferedReader fis = new BufferedReader(new FileReader(filename))) {
            if (fis.ready()) {
                return fis.readLine();
            }
        }
        return null;
    }

    private String readValue() throws FileNotFoundException, RuntimeException, IOException {
        return readFile(valuePath);
    }

    private void writeValue(String value) {
        writeFile(valuePath, value);
//        try {
//            String readVal = readValue();
//            if (!readVal.equals(value)) {
//                throw new RuntimeException("Tried to change pin " + number
//                        + " but failed to write: " + value + ", got " + readVal);
//            }
//        } catch (FileNotFoundException fnfe) {
//            throw new RuntimeException("Permission denied to GPIO file: " + fnfe.getMessage());
//        } catch (SecurityException e) {
//            throw new RuntimeException("Permission denied to GPIO file: " + e.getMessage());
//        } catch (IOException e) {
//            throw new RuntimeException("IO Exception to GPIO file: " + e.getMessage());
//        }
    }

    // in 
    private void setup() {

        //Check to see if the pin is already exported
        //
        if (!Files.exists(Paths.get(valuePath))) {
            writeFile(EXPORT_PATH, String.valueOf(number));
        }
        writeFile(String.format(DIRECTION_PATH, number), "out");

        // Check to see if it doesn't exist before tryping to export it.
//		    if (!new File(FilePaths.getValuePath(pinNumber)).exists()) {
//		        writeFile(FilePaths.getExportPath(), String.valueOf(pinNumber));
//		    }
//			writeFile(FilePaths.getDirectionPath(pinNumber), direction.value);
    }

    private void shutdown() {
        turnOff();
        writeFile(UNEXPORT_PATH, String.valueOf(number));
    }

}
