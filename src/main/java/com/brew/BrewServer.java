/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew;

import com.brew.brewpot.BrewPot;
import com.brew.brewpot.BrewPotConfig;
import com.brew.brewpot.BrewPotState;
import com.brew.config.Configuration;
import com.brew.fermenter.Fermenter;
import com.brew.fermenter.FermenterConfig;
import com.brew.fermenter.FermenterState;
import com.brew.gpio.Pin;
import com.brew.mock.Mocker;
import com.brew.notify.Notifier;
import com.brew.probes.OneWireDevices;
import com.brew.probes.OneWireMonitor;
import com.brew.probes.TemperatureReading;
import com.brew.rest.RecipeService;
import com.brew.rest.BrewService;
import com.brew.rest.ConfigService;
import com.brew.rest.FermenterService;
import com.brew.rest.SessionService;
import com.brew.session.SessionConfig;
import com.brew.session.SessionManager;
import com.brew.websocket.WebSocketNotifier;
import com.brew.websocket.WebSocketServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
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
    
    static WebSocketNotifier<FermenterState> fermStateNotifier;
    static WebSocketNotifier<FermenterConfig> fermConfigNotifier;
    static WebSocketNotifier<BrewPotState> brewPotStateNotifier;
    static WebSocketNotifier<BrewPotConfig> brewPotConfigNotifier;
    static WebSocketNotifier<SessionConfig> sessionConfigNotifier;


    public static void main(String[] args) {
        
        LOG.info("Starting Main application");

        Configuration.load();
        
        if( System.getProperty("root") != null ) {
            LOG.info("Using alternate root: "+System.getProperty("root"));


            LOG.info("One Wire Folder: "+OneWireDevices.getFolder());

            
        }

        if( System.getProperty("mock") != null ) {
            LOG.info("Running Mock Mode");
            Mocker.init();
            Mocker.runMocker();
        }
        
       
        
        //If burner configured, create it..?
        // Burner burner = null;
        // if( Configuration.get().getBurnerGPIO()!=null && Configuration.get().getBurnerProbe()!=null) {
        //     burner = new Burner();
        // }

        BrewPot brewPot = null;
        if( Configuration.get().getBurnerGPIO()!=null && Configuration.get().getBurnerProbe()!=null) {
            brewPot = new BrewPot();
        }
        
        Fermenter fermenter = new Fermenter();
        
        //If ferm configured, create it too?
        
        // burnerNotifier = new WebSocketNotifier<TemperatureReading>("TEMP", "burner");
        // OneWireMonitor.get().monitor(Configuration.get().getBurnerProbe(), burnerNotifier);
        
        // fermNotifier = new WebSocketNotifier<TemperatureReading>("TEMP", "ferm");
        // OneWireMonitor.get().monitor(Configuration.get().getFermenterProbe(), fermNotifier);
        
        // airNotifier = new WebSocketNotifier<TemperatureReading>("TEMP", "air");
        // OneWireMonitor.get().monitor(Configuration.get().getAirProbe(), airNotifier);



        fermStateNotifier = new WebSocketNotifier<FermenterState>();
        Notifier.registerListener(Fermenter.EVENT_FERM_STATE, fermStateNotifier);

        fermConfigNotifier = new WebSocketNotifier<FermenterConfig>();
        Notifier.registerListener(Fermenter.EVENT_FERM_CONFIG, fermConfigNotifier);

        brewPotStateNotifier = new WebSocketNotifier<BrewPotState>();
        Notifier.registerListener(BrewPot.EVENT_BREW_STATE, brewPotStateNotifier);

        brewPotConfigNotifier = new WebSocketNotifier<BrewPotConfig>();
        Notifier.registerListener(BrewPot.EVENT_BREW_CONFIG, brewPotConfigNotifier);

        sessionConfigNotifier = new WebSocketNotifier<SessionConfig>();
        Notifier.registerListener(SessionManager.EVENT_SESSION_CONFIG, sessionConfigNotifier);

        
        // Jetty / Web Server  //
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        
        //Is this even needed?
        //connector.setHost("0.0.0.0");
        connector.setPort(Configuration.get().getPort());
        LOG.info("Starting Web Server on port: {}", Configuration.get().getPort());
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
        //BurnerService burnerService = new BurnerService(burner);
        FermenterService fermenterService = new FermenterService(fermenter);

        SessionManager sessionManager = new SessionManager();
        sessionManager.setBrewPot(brewPot);
        sessionManager.setFermenter(fermenter);
        

        //TODO - temporary for testing..
        sessionManager.startNewSession();

        ResourceConfig rc = new ResourceConfig();
        //rc.register(burnerService);
        rc.register(fermenterService);
        rc.register(MultiPartFeature.class);

        rc.register(new ConfigService());
        rc.register(new BrewService(brewPot));

        // SessionService sessionService = new SessionService();
        rc.register(new RecipeService(sessionManager));
        rc.register(new SessionService(sessionManager));

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
