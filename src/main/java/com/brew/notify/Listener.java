/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.notify;

/**
 *
 * @author andrew.p.davis
 */
public interface Listener<K> {
    
    public void notify(Event<K> e);
    
}
