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
public class BrewSession {

    private State state = State.RECIPE;
    
    

    enum State {
        RECIPE, PREP, MASH, BOIL, COOL, FERM, DONE
    }

    public void setState(State state){
        this.state = state;
    }

    public State getState() {
        return state;
    }

    
}
