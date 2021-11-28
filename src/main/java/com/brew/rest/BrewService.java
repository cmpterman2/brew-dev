/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.rest;

import com.brew.brewpot.BrewPot;
import com.brew.brewpot.BrewPotConfig;
import com.brew.brewpot.BrewPotState;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 *
 * @author andrew.p.davis
 */
@Path("/brew")
public class BrewService {
    
    private BrewPot brewPot;

    public BrewService(BrewPot brewPot){
        this.brewPot = brewPot;
    }
    
    @GET
    @Path("/state")
    @Produces(MediaType.APPLICATION_JSON)
    public BrewPotState getState() {
        return brewPot.getState();
    }

    @GET
    @Path("/config")
    @Produces(MediaType.APPLICATION_JSON)
    public BrewPotConfig getConfig() {
        return brewPot.getConfig();
    }

}
