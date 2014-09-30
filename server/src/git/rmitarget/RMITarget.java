/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.rmitarget;

import amfservices.PGServices;
import amfservices.Reflector;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import libCore.config.Config;
import pgentity.pool.EntityPool;
import target.RemoteTarget;
import target.Request;

/**
 *
 * @author KieuAnh
 */
class RMITarget implements RemoteTarget {
    private final Class<?> amfClass;
    private final Object amfTarget;
    private final Class<?> httpClass;
    private final Object httpTarget;
    
    public RMITarget(Class<?> amfClass, Class<?> httpClass) throws Exception
    {
        this.amfClass = amfClass;
        this.httpClass = httpClass;

        this.amfTarget = amfClass.newInstance();
        this.httpTarget = httpClass.newInstance();
    }
    
    @Override
    public Object doAMF(Request request) throws RemoteException {
        try {
            String rfMethod = request.getMethod();
            Method proc = amfClass.getMethod(rfMethod, String.class,
                    Map.class, Long.TYPE);
            
            return proc.invoke(amfTarget,
                    request.getCaller(), request.getParams(), request.getNow());
        }
        catch (Exception ex) {
            throw new RemoteException("AMF error: ", ex);
        }
        finally
        {
            EntityPool.inst().releaseAllThreadResources();
        }
    }

    @Override
    public Object doHTTP(Request request) throws RemoteException {
        try {
            String rfMethod = request.getMethod();
            Method proc = httpClass.getMethod(rfMethod, Request.class);
            
            return proc.invoke(httpTarget, request);
        }
        catch (Exception ex) {
            throw new RemoteException("HTTP error: ", ex);
        }
        finally
        {
            EntityPool.inst().releaseAllThreadResources();
        }
    }
}