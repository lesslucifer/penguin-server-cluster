/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amfclient.amfconnection;

import com.mycompany.amfclient.Client;
import com.mycompany.amfclient.ConnectionPool;
import com.mycompany.amfclient.Handler;
import com.mycompany.amfclient.Responder;
import com.mycompany.amfclient.amfparams.IParams;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import flex.messaging.io.amf.client.exceptions.ServerStatusException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author suaongmattroi
 */
public class AMFConnection {
    
    public static Responder call(String method, IParams data)
            throws ClientStatusException, ServerStatusException
    {
        flex.messaging.io.amf.client.AMFConnection connection = ConnectionPool.inst().get();
        
        Date lastTime = new Date();
        System.out.println("------> " + method);
        Map<String, Object> resp =
                (Map) connection.call("Service." + method, data.getData());
        
        Date currentTime = new Date();
        
        long deltaTime = currentTime.getTime() - lastTime.getTime();
        ConnectionPool.inst().returnConnection(connection);
        
        return new Responder((Map) resp.get("data"), deltaTime);
    }
    
    public static void callAsync(final String method, final IParams data, final Handler handler)
    {
        new Thread()
        {
            @Override
            public void run() {
                try {
                    Responder resp = call(method, data);
                    handler.handle(resp);
                } catch (ClientStatusException | ServerStatusException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   
        }.start();
    }
}
