/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection.handler;

import minaconnection.interfaces.IPGData;
import minaconnection.event.INotifable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author suaongmattroi
 */
public class MultipleIoHandler extends IoHandlerAdapter implements INotifable{

    private Map<Object, String> listeners = new ConcurrentHashMap<Object, String>();

    private IPGData result;
    
    public MultipleIoHandler() {
    }
    
    // Impl event 
    @Override
    public Object getParams() {
        return this.result;
    }
    
    @Override
    public void register(Object listener, String method) {
        listeners.put(listener, method);
    }

    @Override
    public void unregister(Object listener, String method) {
        listeners.remove(listener);
    }
    
    @Override
    public void notifyEvent() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for(Map.Entry<Object, String> entry : listeners.entrySet())
        {
            Object obj = entry.getKey();
            String method = entry.getValue();
            
            Method m = obj.getClass().getMethod(method, INotifable.class);
            m.invoke(obj, this);
        }
    }
    
    // Override that method to handler event
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    }
    
    @Override
    public final void messageReceived(IoSession session, Object message) throws Exception {
        this.result = (IPGData) message;
        notifyEvent();
    }
    
    @Override
    public final void messageSent(IoSession session, Object message) throws Exception {
    }
    
    // Final method
    @Override
    public final void sessionClosed(IoSession session) throws Exception {
    }
    
    @Override
    public final void sessionCreated(IoSession session) throws Exception {
    }
    
    @Override
    public final void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    }
    
    @Override
    public final void sessionOpened(IoSession session) throws Exception {
    }
}
