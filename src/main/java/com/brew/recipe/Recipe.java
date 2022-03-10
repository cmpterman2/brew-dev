/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.recipe;

/**
 *
 * @author andrew.p.davis
 */
public class Recipe {
    private String name;

    private float preMashTarget = 164.0f;
    private float mashTarget = 155.0f;
    //Total Water Needed
    
    //Water Additions

    //Mash Pre-Target
    //Mash Target

    //Mash Additions
    

    //Boil Volume
    //Boil SG

    //Boil Additions


    //Post Boil Vol
    //Post Boil SG


    //Cooling
    //Whirlpool Hops


    //MEASURE
    //Boil Volume
    //Boil SG
    //Post Boil Vol
    //Post Boil SG
    


    //FERM STUFF



    //SG
    //OG
    

    public String getName() {
        return name;
    }

    public float getPreMashTarget() {
        return preMashTarget;
    }

    public void setPreMashTarget(float preMashTarget) {
        this.preMashTarget = preMashTarget;
    }

    public float getMashTarget() {
        return mashTarget;
    }

    public void setMashTarget(float mashTarget) {
        this.mashTarget = mashTarget;
    }

    public void setName(String name) {
        this.name = name;
    }



}