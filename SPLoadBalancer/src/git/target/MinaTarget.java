/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.target;

import java.lang.reflect.InvocationTargetException;
import minaconnection.ClientHandlerFactory;
import minaconnection.MinaAddress;
import minaconnection.SimpleRequestPoolFactory;
import minaconnection.interfaces.IClientHandler;
import minaconnection.interfaces.IRequestPool;
import target.Request;
import target.Response;
import target.Target;

/**
 *
 * @author suaongmattroi
 */
class MinaTarget implements Target {

    private final MinaAddress address;
    private final IRequestPool pool;
    
    public MinaTarget(MinaAddress address) {
        
        pool = SimpleRequestPoolFactory.create();
        this.address = address;
    }
    
    @Override
    public Object doAMF(Request request) throws InvocationTargetException {
        try {
            IClientHandler cHandler = ClientHandlerFactory.create();
            pool.request(address, request, cHandler);
            return cHandler.doReq();
        } catch (Exception ex) {
            throw new InvocationTargetException(ex);
        }
    }

    @Override
    public Object doHTTP(Request request) throws InvocationTargetException {
        try {
            IClientHandler cHandler = ClientHandlerFactory.create();
            pool.request(address, request, cHandler);
            Response resp = (Response) cHandler.doReq();
            return resp.getData();
        } catch (Exception ex) {
            throw new InvocationTargetException(ex);
        }
    }
}
