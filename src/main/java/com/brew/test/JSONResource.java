/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author andrew.p.davis
 */
@Path("/resource")
public class JSONResource {

    Resource resource =  Resource.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getStringResource() {
        return this.resource.toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putStringResource(Resource json) {

        this.resource.setResource(json.getResource());
        return Response.status(Status.OK).entity(json).build();
    }

}
