/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.test;

/**
 *
 * @author andrew.p.davis
 */
public class Resource {

    private String resource = "fuck";

    private Resource() {
    }

    private static class Holder {
        private static final Resource INSTANCE = new Resource();
    }

    public static Resource getInstance() {
        return Holder.INSTANCE;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return resource;
    }

}