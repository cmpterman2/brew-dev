/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.rest;

import com.brew.session.SessionManager;
import com.brew.session.BrewSession;
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
    public BrewSession getStringResource() {
        if( sessionManager.getCurrentSession() != null ) {
            return sessionManager.getCurrentSession().getBrewSession();
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
