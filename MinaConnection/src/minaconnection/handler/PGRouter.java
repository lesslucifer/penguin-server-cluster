/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection.handler;

import share.data.IPGData;
import minaconnection.interfaces.IServices;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author suaongmattroi
 */
class PGRouter {
    
    private final IServices services;
    
    public PGRouter(IServices services) {
        this.services = services;
    }
    
    public void drive(final String method, final IoSession session, final IPGData message) 
            throws NoSuchMethodException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException 
    {    
        Method m = services.getClass().getMethod(method, IPGData.class);
        IPGData data;
        data = (IPGData) m.invoke(services, message);
        session.write(data);
    }
}
