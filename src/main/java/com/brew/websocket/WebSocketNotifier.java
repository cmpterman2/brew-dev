/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.websocket;

import com.brew.notify.Listener;
import com.brew.probes.OneWireMonitor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrew.p.davis
 */
public class WebSocketNotifier<K> implements Listener<K>{
    
    private String type;
    private String id;
    
    public WebSocketNotifier(String type, String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public void notify(K data) {
        //Need to include the probe ID
        Message message = new Message(type, id, data);
        ObjectMapper mapper = new ObjectMapper();
        String json=null;
        try {
            json = mapper.writeValueAsString(message);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(WebSocketNotifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<WebSocket> webSockets = WebSocket.getConnections();
        for (WebSocket webSocket : webSockets) {
            webSocket.sendMessage(json);
        }
    }
  
}
