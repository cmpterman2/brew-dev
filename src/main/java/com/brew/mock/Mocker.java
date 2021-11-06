package com.brew.mock;

import com.brew.gpio.Pin;
import com.brew.gpio.PinManager;
import com.brew.probes.TemperatureReading;
import com.brew.probes.MockDevices;
import com.brew.config.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Mocker {

    private static final Logger LOG = LoggerFactory.getLogger(Mocker.class);

    private static Thread executor;

    private static final long MIN_WAIT = 5000;

    private static Pin burnerPin;

    public static final void init() {

        //this.gpio = ;
        //this.probe = Configuration.get().getBurnerProbe();
        ///burnerPin = Pin.lookupPin(Configuration.get().getBurnerGPIO());
        //burnerPin.createMock();
        //Register thermometer too..
        //OneWireMonitor.get().monitor(probe, this);

    }


    public static final void runMocker () {
        executor = new Thread() {
            public void run() {
                try {
                    while (true) {


                        Pin pin = PinManager.lookupPin(Configuration.get().getBurnerGPIO());
                        TemperatureReading reading = MockDevices.readTemp(Configuration.get().getBurnerProbe());
                        float value = reading.getTempInC();
                        if( pin.isOn() ) {
                            // Increase temp
                            value = value + .15f;
                            if( value > 100 ) { 
                                value = 100.0f;
                            }
                            
                        } else {
                            value = value - .07f;
                            if( value < 20f ) {
                                value = 20.0f;
                            }

                        }
                        MockDevices.writeTemp(reading.getProbe(), value);



                        pin = PinManager.lookupPin(Configuration.get().getHeatGPIO());
                        Pin coolPin = PinManager.lookupPin(Configuration.get().getCoolGPIO());
                        reading = MockDevices.readTemp(Configuration.get().getFermenterProbe());
                        value = reading.getTempInC();
                        if( pin.isOn() ) {
                            // Increase temp
                            value = value + .15f;
                            if( value > 100 ) { 
                                value = 100.0f;
                            }
                            
                        } else if (coolPin.isOn() ) {
                            value = value - .15f;
                            if( value < 0.0f ) {
                                value = 0.0f;
                            }

                        } else {
                            if( value > 20.0f) {
                                value = value - .07f;
                            }
                            
                            if( value < 20f ) {
                                value = value + .07f;
                            }

                        }
                        MockDevices.writeTemp(reading.getProbe(), value);

                        

                        // //TODO - this was taking 800ms - one at a time.  Should try this out and see if this goes faster in parallel...
                        // //Should we check once per loop or multiple times?
                        // long currentTime = System.currentTimeMillis();
                        // for (Map.Entry<String, Monitor> entry : monitors.entrySet()) {
                        //     Monitor monitor = entry.getValue();

                        //     //Read when necessary - may be more / less often depending on what is going on.  Retry?
                        //     if (currentTime >= monitor.getNextReadTime()) {
                        //         TemperatureReading temp = OneWireDevices.readTemp(monitor.getProbe());

                        //         //long nextReadTime = currentTime;
                        //         //if( temp.isValid()) {
                        //         // Update monitor
                        //         monitor.addTemperatureReading(temp);
                        //         //}
                        //     }
                        // }

                        //TODO Could base this on the next read time...
                        Thread.sleep(MIN_WAIT);

                    }
                } catch (InterruptedException ex) {
                    LOG.warn("Thread interrupted.  Stopping Mocker.");
                }
            }
        };

        executor.setDaemon(true);
        LOG.info("Starting Mocker");
        executor.start();
    }


}
