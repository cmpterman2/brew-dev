/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.rest;

import com.brew.recipe.Recipe;
import com.brew.session.SessionManager;

import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import org.blh.beerxml.ParseException;
import org.blh.beerxml.parser.dom.DOMParser;
import org.blh.beerxml.parser.dom.DOMParserBuilder;
import org.blh.beerxml.type.BeerXMLRecord;
import org.blh.beerxml.type.BeerXMLRecordSet;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author andrew.p.davis
 */
@Path("/recipe")
public class RecipeService {

	SessionManager sessionManager;

    

	
    
    //private List<BeerXMLRecordSet<BeerXMLRecord>> records = null;
    

    public RecipeService(SessionManager sessionManager){
		this.sessionManager = sessionManager;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Recipe getStringResource() {
		return sessionManager.getCurrentSession().getRecipe();
    }
    
    @POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile (
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail)  {

		//String uploadedFileLocation = "c://uploaded/" + fileDetail.getFileName();
		//System.out.println("Called");

		// save it
		//writeToFile(uploadedInputStream, uploadedFileLocation);

		Response.Status status = Response.Status.BAD_REQUEST;

		try {
			List<BeerXMLRecordSet<BeerXMLRecord>> recordSets = DOMParser.defaultDOMParser().parse(uploadedInputStream);

			if( recordSets.size() > 0 ) {
				List<BeerXMLRecord> records = recordSets.get(0).getRecords();
				if( records.size() > 0 ) {
					BeerXMLRecord record = records.get(0);
					if (record instanceof org.blh.beerxml.type.Recipe ){
						org.blh.beerxml.type.Recipe bRecipe = (org.blh.beerxml.type.Recipe) record;
						Recipe recipe = new Recipe();
						recipe.setName(bRecipe.getName());

						sessionManager.getCurrentSession().setRecipe(recipe);

						status = Response.Status.OK;
					}
				}

			}

		} catch( Exception pe) {
			pe.printStackTrace();
		}

		return Response.status(status).build();

	}
        
        
    // save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,
		String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
  
}
