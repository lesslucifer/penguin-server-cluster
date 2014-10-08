/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.target;

import connection.PGAddress;
import connection.PGWaitingResponding;
import connection.SimpleRequestPool;
import connection.data.impl.PGMapData;
import connection.data.interfaces.IPGData;
import target.Request;
import target.Target;

/**
 *
 * @author suaongmattroi
 */
public class SocketTarget implements Target {

    private final PGAddress address;
    private final SimpleRequestPool pool;
    
    public SocketTarget(PGAddress address) {
        
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
