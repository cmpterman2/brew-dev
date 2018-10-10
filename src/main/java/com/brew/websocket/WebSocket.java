/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class WebSocket extends WebSocketAdapter
{
    private static final List<WebSocket> connections = new ArrayList<>();
   
    public WebSocket() {
        System.out.println("Constructor");
    }
    
    public static List<WebSocket> getConnections() {
        return connections;
    }
    
    //Or a notify all..
    
    public Session session;
    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        session = sess;
        synchronized( connections ) {
            connections.add(this);
        }
        System.out.println("Socket Connected: " + sess);
    }
    
    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
        if( message.equals("Hello") ){
            try {
                session.getRemote().sendString("Foo");
            } catch (IOException ex) {
                Logger.getLogger(WebSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Received TEXT message: " + message);
    }
    
    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        synchronized(connections){
            connections.remove(this);
        }
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }
    
    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        //cause.printStackTrace(System.err);
    }
    
    public void sendMessage(String message){ 
        try {
                 session.getRemote().sendString(message);
            } catch (IOException ex) {
                Logger.getLogger(WebSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}