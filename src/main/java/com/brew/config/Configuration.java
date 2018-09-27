/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrew.p.davis
 */
public class Configuration {
    
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);
    
    private static Configuration instance = null;
    
    private Configuration() {
        
    }
  
    public static Configuration get() {
        return instance;
    }
    
    protected static void create() {
        instance = new Configuration();
    }
    
    public static void load() {
        LOG.info("Loading Configuration");
        ObjectMapper mapper = new ObjectMapper();
        try {
            instance = mapper.readValue(new File("config.json"), Configuration.class);
        } catch (IOException ex) {
            LOG.warn("Unable to load", ex);
            throw new RuntimeException("Unable to load configuration, exiting");
        }
    }
    
    public static void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("config.json"), instance);
        } catch (IOException ex) {
            LOG.warn("Unable to save", ex);
        }
    }
    
    
    private String burnerGPIO;
    private String burnerProbe;
    private int port = 8080;

    /**
     * @return the burnerGPIO
     */
    public String getBurnerGPIO() {
        return burnerGPIO;
    }

    /**
     * @param burnerGPIO the burnerGPIO to set
     */
    public void setBurnerGPIO(String burnerGPIO) {
        this.burnerGPIO = burnerGPIO;
    }

    /**
     * @return the burnerProbe
     */
    public String getBurnerProbe() {
        return burnerProbe;
    }

    /**
     * @param burnerProbe the burnerProbe to set
     */
    public void setBurnerProbe(String burnerProbe) {
        this.burnerProbe = burnerProbe;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
    
}
