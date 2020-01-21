/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spnodechecker.client;

import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import flex.messaging.io.amf.client.exceptions.ServerStatusException;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author suaongmattroi
 */
public class AcAuthenticate {
    
    public void authenticateSystem(String url, Map<String, Object> data, Consumer<String> callback)
            throws ClientStatusException, ServerStatusException
    {
        Responder resp = AMFConnection.call(url, "authenticate", 
                PGParamsCreator.getInst().create((String)data.get("uid"), data, null));
        
        callback.accept((String)data.get("sid"));
    }
    
    public void authenticate(String url, Map<String, Object> data, Consumer<String> callback)
            throws ClientStatusException, ServerStatusException
    {
        authenticateSystem(url, data, callback);
    }
}
