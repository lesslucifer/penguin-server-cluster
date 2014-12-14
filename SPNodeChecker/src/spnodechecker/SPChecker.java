/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spnodechecker;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import minaconnection.MinaAddress;
import spnodechecker.client.SPAddress;
import target.Request;
import target.Response;

/**
 *
 * @author suaongmattroi
 */
class SPChecker {
    
    public void check(SpFrCheckerModel model, List<SPAddress> addresses, Map<String, Object> data) throws InvocationTargetException
    {
        if(addresses == null)
            System.out.println("List address is empty");
        
        for(int i = 0; i < addresses.size(); ++i)
        {
            SPAddress address = addresses.get(i);
            MinaTarget target = new MinaTarget(new MinaAddress(address.getAddress(), address.getPort()));
            try {
                Map<String, Object> reqData = new HashMap();
                Request req = Request.makeAMF("", "checkConnection", reqData, (long)0);
                Response respData = (Response) target.doAMF(req);
                if(respData != null)
                {
                    Map result = (Map)respData.getData();
                    Boolean state = (Boolean)result.get("state");
                    if(state)
                        successed(model, address);
                    else
                        failed(model, address);
                }
                else failed(model, address);
            } catch(Exception e) {
                failed(model, address);
            } finally {
                // dispose connection
            }
        }
    }
    
    private void failed(SpFrCheckerModel model, SPAddress address) {
        model.addAddress(address.getAddress() + ":" + address.getPort() + ": failed" );
    }
    
    private void successed(SpFrCheckerModel model, SPAddress address) {
        model.addAddress(address.getAddress() + ":" + address.getPort() + ": successed" );
    }
}
