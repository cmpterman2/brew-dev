/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.probes;

import java.text.DecimalFormat;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andrew.p.davis
 */
public class ThermometerTest {
    
    public ThermometerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        OneWireDevices.setDeviceDirectory(".\\src\\test\\resources\\probes\\");
    }
    
    @AfterClass
    public static void tearDownClass() {
        OneWireDevices.setDeviceDirectory(null);
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of readTempFromFile method, of class Thermometer.
     */
    @Test
    public void testReadTempFromFile() {
        System.out.println("readTempFromFile");

        float result = OneWireDevices.readTemp("28-23231441").getTempInC();
        
        DecimalFormat df = new DecimalFormat("###.##");
        assertEquals("40.02", df.format(result));
        
        result = OneWireDevices.readTemp("28-23231441").getTempInF();
        assertEquals("104.04", df.format(result));
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
    /**
     * Test of readTempFromFile method, of class Thermometer.
     */
    @Test
    public void testListOneWireProbes() {
        System.out.println("listOneWireProbes");
        List<String> result = OneWireDevices.listOneWireProbes();
        assertEquals(1, result.size());
        assertEquals("28-23231441", result.get(0));
        //DecimalFormat df = new DecimalFormat("###.##");
        //assertEquals("40.02", df.format(result));
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
