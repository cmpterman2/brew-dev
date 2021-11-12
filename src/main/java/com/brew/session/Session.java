/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.session;

import java.util.ArrayList;
import java.util.List;

import com.brew.fermenter.FermenterConfig;
import com.brew.fermenter.FermenterState;
import com.brew.notify.Event;
import com.brew.recipe.Recipe;

/**
 *
 * @author andrew.p.davis
 */
public class Session {

    private BrewSession brewSession;
    private Recipe recipe;


    //Should store histories
    //State history

    private List<Event<FermenterState>> fermenterStateHistory = new ArrayList<Event<FermenterState>>();
    private List<Event<FermenterConfig>> fermenterConfigHistory = new ArrayList<Event<FermenterConfig>>();
    

    public BrewSession getBrewSession() {
        return brewSession;
    }

    public List<Event<FermenterConfig>> getFermenterConfigHistory() {
        return fermenterConfigHistory;
    }

    public void setFermenterConfigHistory(List<Event<FermenterConfig>> fermenterConfigHistory) {
        this.fermenterConfigHistory = fermenterConfigHistory;
    }

    public List<Event<FermenterState>> getFermenterStateHistory() {
        return fermenterStateHistory;
    }

    public void setFermenterStateHistory(List<Event<FermenterState>> fermenterStateHistory) {
        this.fermenterStateHistory = fermenterStateHistory;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void setBrewSession(BrewSession brewSession) {
        this.brewSession = brewSession;
    }

    public void addFermState(Event<FermenterState> event) {
        this.fermenterStateHistory.add(event);
    }

    public void addFermConfig(Event<FermenterConfig> event) {
        this.fermenterConfigHistory.add(event);
    }

    

    //private Recipe lo;

    
    
}
