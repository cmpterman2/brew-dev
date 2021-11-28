/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.rest;

import com.brew.session.SessionManager;
import com.brew.fermenter.FermenterConfig;
import com.brew.fermenter.FermenterState;
import com.brew.notify.Event;
import com.brew.session.SessionConfig;

import java.util.List;

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
@Path("/session")
public class SessionService {
    
    SessionManager sessionManager;


    public SessionService(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SessionConfig getStringResource() {
        if( sessionManager.getCurrentSession() != null ) {
            return sessionManager.getCurrentSession().getConfig();
        } else return null;
        
    }

    @GET
    @Path("/config")
    @Produces(MediaType.APPLICATION_JSON)
    public SessionConfig getConfig() {
        if( sessionManager.getCurrentSession() != null ) {
            return sessionManager.getCurrentSession().getConfig();
        } else return null;
        
    }

    @POST
    @Path("/config")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postConfig(SessionConfig config) {
        //ObjectMapper mapper = new ObjectMapper();
        //this.resource.setResource(json.getResource());
        sessionManager.updateConfig(config);
        return Response.status(Response.Status.OK).build();
    }


    @GET
    @Path("/history/fermenter/config")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event<FermenterConfig>> getConfigHistory() {
        if( sessionManager.getCurrentSession() != null ) {
            return sessionManager.getCurrentSession().getFermenterConfigHistory();
        } else return null;
    }

    @GET
    @Path("/history/fermenter/state")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event<FermenterState>> getStateHistory() {
        if( sessionManager.getCurrentSession() != null ) {
            return sessionManager.getCurrentSession().getFermenterStateHistory();
        } else return null;
    }

    // @POST
    // @Consumes(MediaType.APPLICATION_JSON)
    // public Response putStringResource(Config config) {
    //     //ObjectMapper mapper = new ObjectMapper();
    //     //this.resource.setResource(json.getResource());
    //     burner.update(config);
    //     return Response.status(Response.Status.OK).build();
    // }
}
