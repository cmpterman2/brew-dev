/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.gpio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author andrew.p.davis
 */
public class Pin {

    static String MOCK = "/mock";
    static String BASE_PATH = "/sys/class/gpio";
    static String EXPORT_PATH = BASE_PATH + "/export";
    static String UNEXPORT_PATH = BASE_PATH + "/unexport";
    static String DEVICE_PATH = BASE_PATH + "/gpio%d";
    static String DIRECTION_PATH = DEVICE_PATH + "/direction";
    static String VALUE_PATH = DEVICE_PATH + "/value";
    static String ACTIVELOW_PATH = DEVICE_PATH + "/active_low";
    static String BASE_DEVICES = "/sys/devices/";

    //static method to lookup and get pins?
    //Register outpins differently from inpins.
    public static Pattern pinPattern = Pattern.compile("(gpio)([0-9])_([0-9]+)$", Pattern.CASE_INSENSITIVE);

    public static int getPinNumber(String pinName) {

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

    protected static void writeFile(String fileName, String value) throws RuntimeException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(value.getBytes());
        } catch (FileNotFoundException | SecurityException fnfe) {
            throw new RuntimeException("Permission denied to GPIO file: " + fnfe.getMessage());
        } catch (IOException e) {
            System.out.println("GPIO is already exported, continuing");
        }
    }

    protected static String readFile(String filename) throws FileNotFoundException, RuntimeException, IOException {
        try (BufferedReader fis = new BufferedReader(new FileReader(filename))) {
            if (fis.ready()) {
                return fis.readLine();
            }
        }
        return null;
    }

    public static String readValue(int pinNumber) throws FileNotFoundException, RuntimeException, IOException {
        return readFile(getValuePath(pinNumber));
    }

    public static void writeValue(int pinNumber, String incoming) {
        writeFile(getValuePath(pinNumber), incoming);
        try {
            String readVal = readValue(pinNumber);
            if (!readVal.equals(incoming)) {
                throw new RuntimeException("Tried to change pin " + pinNumber
                        + " but failed to write: " + incoming + ", got " + readVal);
            }
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("Permission denied to GPIO file: " + fnfe.getMessage());
        } catch (SecurityException e) {
            throw new RuntimeException("Permission denied to GPIO file: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("IO Exception to GPIO file: " + e.getMessage());
        }
    }

    // in 
    public static void setupPin(int pinNumber) {

        //Check to see if the pin is already exported
        //
        if (!Files.exists(Paths.get(getValuePath(pinNumber)))) {
            writeFile(getExportPath(), String.valueOf(pinNumber));
        }
        writeFile(getDirectionPath(pinNumber), "out");
        
        // Check to see if it doesn't exist before tryping to export it.
//		    if (!new File(FilePaths.getValuePath(pinNumber)).exists()) {
//		        writeFile(FilePaths.getExportPath(), String.valueOf(pinNumber));
//		    }
//			writeFile(FilePaths.getDirectionPath(pinNumber), direction.value);
    }

    public void export() {

    }

    static String getExportPath() {

        if (System.getProperty("debugGPIO") != null) {
            return MOCK + EXPORT_PATH;
        }

        return EXPORT_PATH;
    }

    static String getUnexportPath() {
        if (System.getProperty("debugGPIO") != null) {
            return MOCK + UNEXPORT_PATH;
        }

        return UNEXPORT_PATH;
    }

    public static String getDirectionPath(int pinNumber) {
        if (System.getProperty("debugGPIO") != null) {
            return String.format(MOCK + DIRECTION_PATH, pinNumber);
        }

        return String.format(DIRECTION_PATH, pinNumber);
    }

    public static String getValuePath(int pinNumber) {
        if (System.getProperty("debugGPIO") != null) {
            return String.format(MOCK + VALUE_PATH, pinNumber);
        }

        return String.format(VALUE_PATH, pinNumber);
    }

}
