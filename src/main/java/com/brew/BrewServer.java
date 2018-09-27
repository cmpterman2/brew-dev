/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew;

import com.brew.config.Configuration;
import com.brew.devices.Burner;
import com.brew.rest.BurnerService;
import com.brew.rest.ConfigService;
import com.brew.websocket.WebSocketServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrew.p.davis
 */
public class BrewServer {
    private static final Logger LOG = LoggerFactory.getLogger(BrewServer.class);

    public static void main(String[] args) {
        
        LOG.info("Starting Main application");
        
        Configuration.load();
        
        //If burner configured, create it..?
        Burner burner = null;
        if( Configuration.get().getBurnerGPIO()!=null && Configuration.get().getBurnerProbe()!=null) {
            burner = new Burner();
        }
        
        //If ferm configured, create it too?
        
        
        // Jetty / Web Server  //
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        
        //Is this even needed?
        //connector.setHost("0.0.0.0");
        connector.setPort(Configuration.get().getPort());
        server.addConnector(connector);
        
        
        
        

        // ---STATIC RESOURCE---//
        ServletContextHandler context = new ServletContextHandler();

        context.setContextPath("/");
        context.setResourceBase("../brew-ui/public/");
//        try {
//            context.setBaseResource(Resource.newResource(java.nio.file.Paths.get("C:\\").toUri()));
//        }
//        catch (MalformedURLException m){
//            m.printStackTrace();
//        }
        
        context.addServlet(DefaultServlet.class, "/");
        
        
        
        // Jersey Stuff 
        BurnerService burnerService = new BurnerService(burner);

        ResourceConfig rc = new ResourceConfig();
        rc.register(burnerService);
        rc.register(new ConfigService());

        ServletContainer sc = new ServletContainer(rc);
//
        ServletHolder servletHolder = new ServletHolder(sc);

        //context.setContextPath("/service");
        context.addServlet(servletHolder, "/services/*");
        
        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", WebSocketServlet.class);
        context.addServlet(holderEvents, "/events/*");
        
        //DefaultServlet defaultServlet = new DefaultServlet();
        //defaultServlet.
        
        

//        
//        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        context.setContextPath("/servlet/");
//
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{context, new DefaultHandler()});
        server.setHandler(handlers);
//
//        // Setup the basic application "context" for this application at "/"
//        // This is also known as the handler tree (in jetty speak)
//
////        server.setHandler(context);
////        
//        // Add a websocket to a specific path spec
//        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
//        context.addServlet(holderEvents, "/events/*");
//        
////        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/foo/*");
////        jerseyServlet.setInitOrder(0);
////
////        // Tells the Jersey Servlet which REST service/class to load.
////        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "com.brew");
//
        try {
            server.start();
            //server.dump(System.err);
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }

    }
}
