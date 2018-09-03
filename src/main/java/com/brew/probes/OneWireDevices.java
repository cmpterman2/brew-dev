/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.probes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrew.p.davis
 */
public class OneWireDevices {
    
    private static final Logger LOG = LoggerFactory.getLogger(OneWireDevices.class);
    
    private static final String W1_DEVICES = "/sys/bus/w1/devices/";
    private static final String W1_SLAVE = "/w1_slave";
    
    private static String deviceDirectory = W1_DEVICES;
    
    private OneWireDevices() {
        
    }
    
    public static void setDeviceDirectory(String deviceDir) {
        if( deviceDir == null ) {
            deviceDir = W1_DEVICES;
        }
        deviceDirectory = deviceDir;
    }
    
    public static List<String> listOneWireProbes()
    {
        List<String> probes = new ArrayList();
        
        // try to access the list of 1-wire devices
        File w1Folder = new File(deviceDirectory);
        if (w1Folder.exists()) {
            File[] listOfFiles = w1Folder.listFiles();
            if( listOfFiles != null ) {
                for (File currentFile : listOfFiles) {
                    if (currentFile.isDirectory() && currentFile.getName().startsWith("28")) {
                        probes.add(currentFile.getName());
                    }
                }
            }
        }
        
        if( probes.isEmpty()) {
            LOG.warn("Unable to locate any temperature probes!");
            LOG.warn("Directory found: {}", w1Folder.exists());
        }
        
        return probes;
    }
    
    /**
     * @param probe
     * @return The current temperature read directly from the file system.
     */
    public static TemperatureReading readTemp(String probe) {
        
        long startTime = System.currentTimeMillis();
        float temp = Float.NaN;
        
        String fileName = deviceDirectory + '/' + probe + '/' + W1_SLAVE;
        
        try (BufferedReader br = new BufferedReader(new FileReader(fileName)) )
        {
            String line = br.readLine();
            
            if (line == null || line.contains("NO")) {
                // bad CRC, do nothing
                LOG.warn("Bad CRC from {}", fileName);
            } else if (line.contains("YES")) {
                // good CRC
                line = br.readLine();
                LOG.debug("Raw contents of temp: {}", line);
                // last value should be t=
                String sTemp = line.substring(line.indexOf("t=")+2);
                
                temp = Float.parseFloat(sTemp)/1000.0f;
            } 
        } catch (IOException ie) {
            LOG.warn("IOException when reading file", ie);
        } catch (NumberFormatException nfe) {
            LOG.warn("Unable to parse temperature", nfe);
        } 
        
        long endTime = System.currentTimeMillis();
        
        LOG.debug("Read temperature of {}C.  Took {} ms.", temp, endTime-startTime);
        
        return new TemperatureReading(probe, startTime, temp);
    }
}
