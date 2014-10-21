/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author suaongmattroi
 */
class NotifableCounter {
    
    private class Listener {
        private final Object listener;
        private final String method;
        
        public Listener(Object listener, String method) {
            this.listener = listener;
            this.method = method;
        }
    }
    
    private Listener listener;
    
    private static final String methodHandleEvent = "onEventCaught";
    
    private final Map<String, INotifable> notifables;
    
    private int counter;
    private int nOfNotifables;
    
    public NotifableCounter() {
        notifables  = new ConcurrentHashMap<>();
    }
    
    public Map<String, INotifable> getNotifables() {
        return this.notifables;
    }
    
    public void register(Object listener, String method) {
        this.listener = new Listener(listener, method);
    }
    
    public void addNotifable(INotifable notifable, String identify) {
        this.notifables.put(identify, notifable);
        notifable.register(this, methodHandleEvent);
        ++nOfNotifables;
    }
    
    public void removeNotifable(String identify) {
        if(this.notifables.containsKey(identify))
        {
            INotifable notifable = this.notifables.remove(identify);
            notifable.unregister(this, methodHandleEvent);
            --nOfNotifables;
        }
    }
    
    private void dispatch() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        if(this.listener != null) {
            Method m = listener.listener.getClass().getMethod(listener.method, new Class[]{});
            m.invoke(listener.listener, new Object[]{});
        }
    }
    
    public void onEventCaught(INotifable dispatcher) {
        ++counter;
        if(counter >= nOfNotifables)
        {
            try {
                resetCounter();
                dispatch();
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                Logger.getLogger(NotifableCounter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void resetCounter() {
        this.counter = 0;
    }
}
