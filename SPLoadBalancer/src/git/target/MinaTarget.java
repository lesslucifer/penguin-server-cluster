/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.target;

import minaconnection.PGAddress;
import minaconnection.PGWaitingResponding;
import minaconnection.SimpleRequestPool;
import share.data.PGMapData;
import minaconnection.interfaces.IMinaData;
import share.data.PGDataType;
import target.Request;
import target.Target;

/**
 *
 * @author suaongmattroi
 */
public class MinaTarget implements Target {

    private final PGAddress address;
    private final SimpleRequestPool pool;
    
    public MinaTarget(PGAddress address) {
        
        pool = new SimpleRequestPool();
        this.address = address;
    }
    
    @Override
    public Object doAMF(Request request) {
        IMinaData msg = new PGMapData(pool.nextIndex(), 
                request.getCaller(),
                request.getMethod(),
                request.getParams(),
                request.getNow(),
                PGDataType.AMF);
        PGWaitingResponding wresp = new PGWaitingResponding();
        pool.request(address, msg, PGWaitingResponding.RESP_FUNC, wresp);
        return wresp.doReq();
    }

    @Override
    public Object doHTTP(Request request) {
        IMinaData msg = new PGMapData(pool.nextIndex(), 
            request.getCaller(),
            request.getMethod(),
            request.getParams(),
            request.getNow(),
            PGDataType.HTTP);
        PGWaitingResponding wresp = new PGWaitingResponding();
        pool.request(address, msg, PGWaitingResponding.RESP_FUNC, wresp);
        IMinaData data = wresp.doReq();
        return data.getData();
    }
}
