/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 *
 * @author andrew.p.davis
 */
public class WebSocketServlet extends org.eclipse.jetty.websocket.servlet.WebSocketServlet{

    @Override
    public void configure(WebSocketServletFactory factory)
    {
        factory.register(WebSocket.class);
    }
    
}
