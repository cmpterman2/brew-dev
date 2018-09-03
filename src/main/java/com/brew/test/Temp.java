/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import org.owfs.jowfsclient.Enums;
//import org.owfs.jowfsclient.OwfsConnection;
//import org.owfs.jowfsclient.OwfsConnectionConfig;
//import org.owfs.jowfsclient.OwfsConnectionFactory;
//import org.owfs.jowfsclient.OwfsException;

/**
 *
 * @author andrew.p.davis
 */
public class Temp {
    
//    public static final int DEFAULT_OWFS_PORT = 4304;
//    
//    /**
//     * One Wire File System Connection.
//     */
//    public static OwfsConnection owfsConnection = null;
//    /**
//     * Flag whether the user has selected OWFS.
//     */
//    public static boolean useOWFS = false;
//    /**
//     * The Default OWFS server name.
//     */
//    public static String owfsServer = "localhost";
//    /**
//     * The OWFS port value.
//     */
//    public static Integer owfsPort = DEFAULT_OWFS_PORT;
//    
//    public static void main(String[] args) {
//        
//    }
//    
//    
//    
//    /***********
//     * Helper method to read a path value from OWFS with all checks.
//     *
//     * @param path
//     *            The path to read from
//     * @return A string representing the value (or null if there's an error
//     * @throws OwfsException
//     *             If OWFS throws an error
//     * @throws IOException
//     *             If an IO error occurs
//     */
//    public static String readOWFSPath(final String path) throws OwfsException,
//            IOException {
//        String result = "";
//        if (owfsConnection == null) {
//            setupOWFS();
//            if (owfsConnection == null) {
//                //BrewServer.LOG.info("no OWFS connection");
//                return "";
//            }
//        }
//        try {
//            if (owfsConnection.exists(path)) {
//                result = owfsConnection.read(path);
//            }
//        } catch (OwfsException e) {
//            // Error -1 is file not found, exists should bloody catch this
//            if (!e.getMessage().equals("Error -1")) {
//                throw e;
//            }
//        }
//
//        return result.trim();
//    }
//    
//    /**
//     * Create the OWFSConnection configuration in a thread safe manner.
//     */
//    public static void setupOWFS() {
//        if (owfsConnection != null) {
//            try {
//                owfsConnection.disconnect();
//                owfsConnection = null;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (!useOWFS)
//        {
//            return;
//        }
//        // Use the thread safe mechanism
//        //BrewServer.LOG.info("Connecting to " + owfsServer + ":" + owfsPort);
//        try {
//            OwfsConnectionConfig owConfig = new OwfsConnectionConfig(owfsServer,
//                    owfsPort);
//            owConfig.setPersistence(Enums.OwPersistence.ON);
//            owfsConnection = OwfsConnectionFactory
//                    .newOwfsClientThreadSafe(owConfig);
//            useOWFS = true;
//        } catch (NullPointerException npe) {
//            //BrewServer.LOG.warning("OWFS is not able to be setup. You may need to rerun setup.");
//        }
//    }
//    
//    
//    // ex - call with "/28"
//    
//    // Should this remove the leading slash from input, outputs?
//     public static List<String> getOneWireDevices(String prefix) {
//        List<String> devices;
//        devices = new ArrayList<>();
//        if (owfsConnection == null) {
//            //LaunchControl.setMessage("OWFS is not setup,"
//            //        + " please delete your configuration file and start again");
//            return devices;
//        }
//        try {
//            List<String> owfsDirs = owfsConnection.listDirectory("/");
//            if (owfsDirs.size() > 0) {
//               // BrewServer.LOG.info("Listing OWFS devices on " + owfsServer
//                //        + ":" + owfsPort);
//            }
//            Iterator<String> dirIt = owfsDirs.iterator();
//            String dir;
//
//            while (dirIt.hasNext()) {
//                dir = dirIt.next();
//                if (dir.startsWith(prefix)) {
//                    devices.add(dir);
//                }
//            }
//        } catch (OwfsException | IOException e) {
//            e.printStackTrace();
//        }
//
//        return devices;
//    }
//    
}
