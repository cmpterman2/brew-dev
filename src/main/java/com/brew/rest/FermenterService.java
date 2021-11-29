/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.rest;

import com.brew.fermenter.Fermenter;
import com.brew.fermenter.FermenterConfig;
import com.brew.fermenter.FermenterState;

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
@Path("/fermenter")
public class FermenterService {
    
    Fermenter fermenter;
    public FermenterService(Fermenter fermenter){
        this.fermenter = fermenter;
    }
    
    @GET
    @Path("/state")
    @Produces(MediaType.APPLICATION_JSON)
    public FermenterState getState() {
        return fermenter.getState();
    }

    @GET
    @Path("/config")
    @Produces(MediaType.APPLICATION_JSON)
    public FermenterConfig getConfig() {
        return fermenter.getConfig();
    }

    //Combined object
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Data getStringResource() {
        Data data = new Data();
        data.setConfig(fermenter.getConfig());
        data.setState(fermenter.getState());
        return data;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putStringResource(FermenterConfig config) {
        //ObjectMapper mapper = new ObjectMapper();
        //this.resource.setResource(json.getResource());
        fermenter.update(config);
        return Response.status(Response.Status.OK).build();
    }


    public class Data {
        private FermenterState state;
        private FermenterConfig config;
        public FermenterState getState() {
            return state;
        }
        public FermenterConfig getConfig() {
            return config;
        }
        public void setConfig(FermenterConfig config) {
            this.config = config;
        }
        public void setState(FermenterState state) {
            this.state = state;
        }
    }
}
