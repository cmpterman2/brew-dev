/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.session;

import com.brew.recipe.Recipe;

/**
 *
 * @author andrew.p.davis
 */
public class Session {

    private BrewSession brewSession;
    private Recipe recipe;

    public BrewSession getBrewSession() {
        return brewSession;
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

    //private Recipe lo;

    
    
}
