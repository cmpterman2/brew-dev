/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.session;

/**
 *
 * @author andrew.p.davis
 */
public class SessionManager {
    
    private Session session;



    public SessionManager() {
        //Temporary
        session = new Session();
        session.setBrewSession(new BrewSession());

    }

    public Session getCurrentSession() {
        return this.session;
    }



    
    
}
