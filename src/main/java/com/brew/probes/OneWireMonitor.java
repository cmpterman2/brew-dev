/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brew.probes;

import com.brew.notify.Listener;
import com.brew.notify.Notifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrew.p.davis
 */
public class OneWireMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(OneWireMonitor.class);

    private static final boolean MOCK = (System.getProperty("mock") != null );

    private Map<String, Monitor> monitors = new HashMap();
    private static OneWireMonitor instance = new OneWireMonitor();
    private static final long MAX_WAIT = 5000;
    private static final long MIN_WAIT = 100;

    private Thread executor;

    private OneWireMonitor() {
        executor = new Thread() {
            public void run() {
                try {
                    while (true) {

                        //TODO - this was taking 800ms - one at a time.  Should try this out and see if this goes faster in parallel...
                        //Should we check once per loop or multiple times?
                        long currentTime = System.currentTimeMillis();
                        for (Map.Entry<String, Monitor> entry : monitors.entrySet()) {
                            Monitor monitor = entry.getValue();

                            //Read when necessary - may be more / less often depending on what is going on.  Retry?
                            if (currentTime >= monitor.getNextReadTime()) {
                                TemperatureReading temp;
                                if( MOCK ) {
                                    temp = MockDevices.readTemp(monitor.getProbe());
                                } else {
                                    temp = OneWireDevices.readTemp(monitor.getProbe());
                                }

                                //long nextReadTime = currentTime;
                                //if( temp.isValid()) {
                                // Update monitor
                                monitor.addTemperatureReading(temp);
                                //}
                            }
                        }

                        //TODO Could base this on the next read time...
                        Thread.sleep(MIN_WAIT);

                    }
                } catch (InterruptedException ex) {
                    LOG.warn("Thread interrupted.  Stopping all monitors.");
                }
            }
        };

        executor.setDaemon(true);
        LOG.info("Starting One Wire Monitor");
        executor.start();
    }

    public static OneWireMonitor get() {
        return instance;
    }

    public synchronized void monitor(String probe, Listener listener) {
        Monitor monitor = this.monitors.get(probe);
        if (monitor == null) {
            LOG.info("Adding new monitored probe {}", probe);
            monitor = new Monitor(probe);
            monitors.put(probe, monitor);
        }

        monitor.addListener(listener);
    }

    private class Monitor {

        private String probe;
        private List<Listener> listeners;
        private TemperatureReading lastTemp;
        private LinkedList<TemperatureReading> history = new LinkedList();
        private long nextReadTime;
        private static final int HISTORY_LIMIT = 4;
        private static final float THRESHOLD = 0.07f;
        private static final float REASONABLE_THRESHOLD = 25.0f;
        private static final float MAX_TEMP = 110.0f;
        private static final float MIN_TEMP = -30.0f;

        public boolean isDifferent(TemperatureReading reading) {

            return lastTemp == null || Math.abs(reading.getTempInC() - lastTemp.getTempInC()) > THRESHOLD;
        }

        public boolean shouldNotify(TemperatureReading reading) {

            return lastTemp == null || 
                    (Math.abs(lastTemp.getTempInC() - calculateAverageTempInC()) > THRESHOLD && Math.abs(reading.getTempInC() - lastTemp.getTempInC()) > THRESHOLD);
        }

        public boolean isValid(TemperatureReading temp) {
            return temp.getTempInC() > MIN_TEMP && temp.getTempInC() < MAX_TEMP;
        }

        public boolean isGoodReading(TemperatureReading newTemp, TemperatureReading oldTemp) {
            return oldTemp == null || Math.abs(newTemp.getTempInC() - oldTemp.getTempInC()) < REASONABLE_THRESHOLD;
        }

        public Monitor(String probe) {
            this.probe = probe;
            listeners = new ArrayList();
            lastTemp = null;
            nextReadTime = 0L;
        }

        public long getNextReadTime() {
            return nextReadTime;
        }

        public void addListener(Listener listener) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }

        public void addTemperatureReading(TemperatureReading reading) {
            //TODO Improve temperature measurements
            // Temp different
            // Bad Reading (IE, check for major / unexpected change)
            // Temp same?
            // Reduce chatter / unsignificant change
            // Compare with history...
            if (isValid(reading)) {

                if (!isGoodReading(reading, lastTemp)) {
                    //LOG bad readings for now...?
                    //Should keep track and if it repeats, then it's probably right...
                    LOG.warn("Potentially bad temperature reading: OLD: {}, NEW: {}", lastTemp, reading);
                }

                //Add to history...
                addToHistory(reading);
                
                if (shouldNotify(reading)) {
                    Notifier.notifyListeners(listeners, reading);
                    lastTemp = reading;
                } else if (isDifferent(reading)){
                    nextReadTime = reading.getReadTime() + 1000;
                } else {
                    nextReadTime = reading.getReadTime() + MAX_WAIT;
                } 
            }
        }

        protected void addToHistory(TemperatureReading reading) {
            history.add(reading);
            if (history.size() > HISTORY_LIMIT) {
                history.removeFirst();
            }
        }

        protected float calculateAverageTempInC() {
            float count = 0.0f;
            float average = 0.0f;
            for (TemperatureReading reading : history) {
                average += reading.getTempInC();
                count += 1.0f;
            }
            average = average / count;
            LOG.debug("Average temp: {}", average);
            return average;
        }

        public String getProbe() {
            return probe;
        }

        private TemperatureReading getLastTemp() {
            return lastTemp;
        }

    }

}
