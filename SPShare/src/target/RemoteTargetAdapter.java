/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package target;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

/**
 *
 * @author KieuAnh
 */
public class RemoteTargetAdapter implements Target {
    private final RemoteTarget remote;

    public RemoteTargetAdapter(RemoteTarget remote) {
        this.remote = remote;
    }
    
    @Override
    public Boolean isGood() {
        return true;
    }
    
    @Override
    public Object doAMF(Request request) throws InvocationTargetException {
        try {
            return remote.doAMF(request);
        } catch (RemoteException ex) {
            throw new InvocationTargetException(ex);
        }
    }

    @Override
    public Object doHTTP(Request request) throws InvocationTargetException {
        try {
            return remote.doHTTP(request);
        } catch (RemoteException ex) {
            throw new InvocationTargetException(ex);
        }
    }
    
}
