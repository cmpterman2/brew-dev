/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.websocket;

import com.brew.config.Configuration;
import com.brew.notify.Listener;
import com.brew.probes.OneWireMonitor;
import com.brew.probes.TemperatureReading;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrew.p.davis
 */
public class WebSocketNotifier implements Listener<TemperatureReading>{
    
    public void registerListeners() {
        OneWireMonitor.get().monitor(Configuration.get().getBurnerProbe(), this);
    }

    @Override
    public void notify(TemperatureReading notification) {
        Message message = new Message("TEMP", notification);
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
