/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rmitarget;

import amfservices.PGServices;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Map;
import pgentity.pool.EntityPool;
import target.RemoteTarget;
import target.Request;

/**
 *
 * @author KieuAnh
 */
class RMITarget implements RemoteTarget {
    private final PGServices services = new PGServices();
    
    @Override
    public Object doAMF(Request request) throws RemoteException {
        try {
            String rfMethod = "rf_" + request.getMethod();
            Method proc = PGServices.class.getMethod(rfMethod, String.class,
                    Map.class, Long.TYPE);
            
            return proc.invoke(services,
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
        Throwable ex = new UnsupportedOperationException("Not supported yet.");
            throw new RemoteException("HTTP error: ", ex);
    }
}
