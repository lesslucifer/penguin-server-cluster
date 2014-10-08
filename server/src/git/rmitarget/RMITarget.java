/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.rmitarget;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Map;
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
            
            Object resp = proc.invoke(amfTarget,
                    request.getCaller(), request.getParams(), request.getNow());
            if (check(resp))
            {
                return resp;
            }
            else
            {
                return Collections.EMPTY_MAP;
            }
        }
        catch (Exception ex) {
            throw new RemoteException("AMF error: ", ex);
        }
        finally
        {
            EntityPool.inst().releaseAllThreadResources();
        }
    }
    
    private boolean check(Object x)
    {
        if (x instanceof String)
            return true;
        
        if (x instanceof Number)
            return true;
        
        if (x instanceof Boolean)
            return true;
        
        if (x instanceof Map)
        {
            Map<String, Object> m = (Map) x;
            for (Map.Entry<String, Object> entrySet : m.entrySet()) {
                String key = entrySet.getKey();
                Object value = entrySet.getValue();
                
                if (!check(value))
                {
                    return false;
                }
            }
            
            return true;
        }
        
        return false;
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