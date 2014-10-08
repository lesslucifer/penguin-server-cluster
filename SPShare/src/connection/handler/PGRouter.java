/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package connection.handler;

import connection.data.interfaces.IPGData;
import connection.data.interfaces.IServices;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author suaongmattroi
 */
public class PGRouter {
    
    private IServices services;
    
    public PGRouter(IServices services) {
        this.services = services;
    }
    
    public void drive(final String method, final IoSession session, final IPGData message) {
        
        // Need to create thread
        new Thread() {
           @Override
           public void run() {
                if(services != null) {
                    IPGData data;
                    try {
                        Method m = services.getClass().getMethod(method, IPGData.class);
                        data = (IPGData) m.invoke(services, message);
                        session.write(data);
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | 
                            IllegalArgumentException | InvocationTargetException ex) 
                    {
                        // Return error, wait for format
                        System.out.println(ex.getMessage());
                    }
                }
           }
        }.start();
    }
}
