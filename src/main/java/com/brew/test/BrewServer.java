/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.test;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 *
 * @author andrew.p.davis
 */
public class BrewServer {
    
    

    private static final int PORT = 8080;

    public static void main(String[] args) {
//        Server jettyServer = new Server(8080);
//

//
//        // ---STATIC RESOURCE---//
//        ResourceHandler staticResourceHandler = new ResourceHandler();
//
//        staticResourceHandler.setResourceBase("./src/webapp/");
//
//        ContextHandler staticContextHandler = new ContextHandler("/");
//        staticContextHandler.setHandler(staticResourceHandler);
//
//        // ---ADD HANDLERS---//
//        HandlerList handlers = new HandlerList();
//
//        handlers.setHandlers(new Handler[]{jsonResourceContext, //
//            staticContextHandler, //
//            new DefaultHandler() //
//    });
//
//        jettyServer.setHandler(handlers);
//
//        try {
//
//            jettyServer.start();
//            jettyServer.join();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            jettyServer.destroy();
//        }
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(PORT);
        server.addConnector(connector);

        // ---STATIC RESOURCE---//
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{"index.html"});
        //directory where files are served
        resource_handler.setResourceBase(".");

        ContextHandler staticContextHandler = new ContextHandler("/");
        staticContextHandler.setHandler(resource_handler);

        JSONResource resource = new JSONResource();

        ResourceConfig rc = new ResourceConfig();
        rc.register(resource);

        ServletContainer sc = new ServletContainer(rc);
//
        ServletHolder servletHolder = new ServletHolder(sc);
//
        ServletContextHandler jsonResourceContext = new ServletContextHandler();
        jsonResourceContext.addServlet(servletHolder, "/*");

//        
//        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        context.setContextPath("/servlet/");
//
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{jsonResourceContext, staticContextHandler, new DefaultHandler()});
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
            server.dump(System.err);
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }

    }
}
