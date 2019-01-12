/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.rest;

import com.brew.devices.BurnerData;
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
@Path("/brew")
public class BrewService {
    
    private List<BeerXMLRecordSet<BeerXMLRecord>> records = null;
    

    public BrewService(){

    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<BeerXMLRecordSet<BeerXMLRecord>> getStringResource() {
        return records;
    }
    
    @POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile (
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail)  {

		String uploadedFileLocation = "c://uploaded/" + fileDetail.getFileName();

		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);
                try {
                    records = DOMParser.defaultDOMParser().parse(new File(uploadedFileLocation));
                } catch( Exception pe) {
                    pe.printStackTrace();
                }
                

		String output = "File uploaded to : " + uploadedFileLocation;

		return Response.status(200).entity(output).build();

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
