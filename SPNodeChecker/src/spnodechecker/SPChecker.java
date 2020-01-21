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
    
    public Map<SPAddress, String> check(List<SPAddress> addresses, Map<String, Object> data) throws InvocationTargetException
    {
        if(addresses == null)
            System.out.println("List address is empty");
     
        Map<SPAddress, String> results = new HashMap();
        
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
                        successed(results, address);
                    else
                        failed(results, address);
                }
                else failed(results, address);
            } catch(Exception e) {
                failed(results, address);
            } finally {
            }
        }
        return results;
    }
    
    // Use for UI checker
//    public void check(SpFrCheckerModel model, List<SPAddress> addresses, Map<String, Object> data) throws InvocationTargetException
//    {
//        if(addresses == null)
//            System.out.println("List address is empty");
//        
//        for(int i = 0; i < addresses.size(); ++i)
//        {
//            SPAddress address = addresses.get(i);
//            MinaTarget target = new MinaTarget(new MinaAddress(address.getAddress(), address.getPort()));
//            try {
//                Map<String, Object> reqData = new HashMap();
//                Request req = Request.makeAMF("", "checkConnection", reqData, (long)0);
//                Response respData = (Response) target.doAMF(req);
//                if(respData != null)
//                {
//                    Map result = (Map)respData.getData();
//                    Boolean state = (Boolean)result.get("state");
//                    if(state)
//                        successed(model, address);
//                    else
//                        failed(model, address);
//                }
//                else failed(model, address);
//            } catch(Exception e) {
//                failed(model, address);
//            } finally {
//            }
//        }
//    }
    
    private void failed(Map<SPAddress, String> result, SPAddress address) {
//        model.addAddress(address.getAddress() + ":" + address.getPort() + ": failed" );
        result.put(address, "failed");
    }
    
    private void successed(Map<SPAddress, String> result, SPAddress address) {
//        model.addAddress(address.getAddress() + ":" + address.getPort() + ": successed" );
        result.put(address, "successed");
    }
}
