package com.brew.notify;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Notifier {
    
    private static final int THREAD_POOL_SIZE = 10;
    
    private static final Notifier instance = new Notifier();
    private static final ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private static final List tempListeners = new ArrayList();
    
    private Notifier() {
        
    }
    
    public static void notifyListeners(List<Listener> listeners, Object notification)
    {
        listeners.stream().map((listener) -> new Runnable() {
            public void run() {
                listener.notify(notification);
            }
        }).forEachOrdered((task) -> {
            pool.execute(task );
        });
    }
    
    //Do we want to call notify with the last value?  
    public static void registerListener(Object listener) {
        tempListeners.add(listener);
    }
    
    public static void notifyTempChange(String id, float temp){
        for (Object tempListener : tempListeners) {
            Runnable task = new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Notifier.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println(tempListener.toString());
                }
            };
            pool.execute(task );
        }
    }
    
    
    private class notifyTask implements Runnable {
        
        
        public void run()
        {
           
        }
    }
            
            
}
