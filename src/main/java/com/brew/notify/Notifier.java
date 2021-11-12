package com.brew.notify;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class Notifier {
    
    private static final int THREAD_POOL_SIZE = 10;
    private static final ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private static final HashMap<String, List<Listener>> listenerMap = new HashMap<String, List<Listener>>();



    public static synchronized void registerListener(String type, Listener listener) {
        List<Listener> listeners = listenerMap.get(type);
        if( listeners == null ) {
            listeners = new ArrayList<Listener>();
            listenerMap.put(type, listeners);
        }

        //Double check for duplicates
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }

    }

    public static synchronized void notifyListeners(Event event)
    {
        List<Listener> listeners = listenerMap.get(event.getType());
        if( listeners != null ) {
            listeners.stream().map((listener) -> new Runnable() {
                public void run() {
                    listener.notify(event);
                }
            }).forEachOrdered((task) -> {
                pool.execute(task );
            });
        }
    }      
            
}
