package com.brew.probes;

import java.util.HashMap;

public class MockDevices {

    private static final HashMap<String, Float> probeMap = new HashMap();

    private static final float DEFAULT = 20.0f;

    private float tempInC;
    private String probe;

    public static TemperatureReading readTemp(String probe) {
        Float value = probeMap.get(probe);
        if( value == null ){
            value = DEFAULT;
            writeTemp(probe, DEFAULT);
        }

        return new TemperatureReading(probe, System.currentTimeMillis(), value);


    }

    public static void writeTemp(String probe, float tempInC) {
        probeMap.put(probe, tempInC);
    }
}