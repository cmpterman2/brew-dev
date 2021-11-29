/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.rest;

import com.brew.config.Configuration;
import com.fasterxml.jackson.core.JsonProcessingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author andrew.p.davis
 */
@Path("/config")
public class ConfigService {
    
    public ConfigService(){
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Configuration getStringResource() {
        return Configuration.get();
    }

}
