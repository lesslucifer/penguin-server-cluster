/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.target;

import minaconnection.ClientHandlerFactory;
import minaconnection.MinaAddress;
import minaconnection.SimpleRequestPoolFactory;
import minaconnection.interfaces.IClientHandler;
import minaconnection.interfaces.IRequestPool;
import share.data.IPGData;
import share.data.PGDataType;
import share.data.PGMapData;
import target.Request;
import target.Target;

/**
 *
 * @author suaongmattroi
 */
public class MinaTarget implements Target {

    private final MinaAddress address;
    private final IRequestPool pool;
    
    public MinaTarget(MinaAddress address) {
        
        pool = SimpleRequestPoolFactory.create();
        this.address = address;
    }
    
    @Override
    public Object doAMF(Request request) {
        IPGData msg = new PGMapData(
                request.getCaller(),
                request.getMethod(),
                request.getParams(),
                request.getNow(),
                PGDataType.AMF);
        IClientHandler cHandler = ClientHandlerFactory.create();
        pool.request(address, msg, cHandler);
        IPGData data = (IPGData) cHandler.doReq();
        return data.data();
    }

    @Override
    public Object doHTTP(Request request) {
        IPGData msg = new PGMapData( 
            request.getCaller(),
            request.getMethod(),
            request.getParams(),
            request.getNow(),
            PGDataType.HTTP);
        IClientHandler cHandler = ClientHandlerFactory.create();
        pool.request(address, msg, cHandler);
        IPGData data = (IPGData) cHandler.doReq();
        return data.data();
    }
}
