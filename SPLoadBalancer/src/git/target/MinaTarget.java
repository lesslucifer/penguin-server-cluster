/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.target;

import minaconnection.SimpleRequestPool;
import minaconnection.PGAddress;
import minaconnection.PGWaitingResponding;
import minaconnection.data.impl.PGMapData;
import minaconnection.interfaces.IPGData;
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
        
        if(this.address != null)
        {
            IPGData msg = new PGMapData(pool.getIndex(), 
                    request.getCaller(), request.getMethod(), request.getParams(), request.getNow());
            PGWaitingResponding wresp = new PGWaitingResponding();
            pool.request(address, msg, PGWaitingResponding.RESP_FUNC, wresp);
            try {
                IPGData data = wresp.doReq();
                return data.getData();
            } catch(Exception ex) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Object doHTTP(Request request) {
        return null;
    }
}
