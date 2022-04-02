/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.rest;

import com.brew.ispindel.ISpindelData;

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
@Path("/ispindel")
public class ISpindelService {

    ISpindelData lastReceived = new ISpindelData();
    
    
    public ISpindelService(){
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ISpindelData getState() {
        return lastReceived;
    }

    @POST  
    @Path("/test")
    public Response postTestData(String data) {
        System.out.println(data);
        return Response.status(Response.Status.OK).build();
    }

    @POST  
    //@Path("/test")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postData(ISpindelData data) {
        lastReceived = data;
        return Response.status(Response.Status.OK).build();
    }


}
