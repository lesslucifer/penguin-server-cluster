/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spnodechecker.client;

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
    
    public static Responder call(String url, String method, IParams data)
            throws ClientStatusException, ServerStatusException
    {
        flex.messaging.io.amf.client.AMFConnection connection = 
                new flex.messaging.io.amf.client.AMFConnection();
        connection.connect(url);
        
        Date lastTime = new Date();
        System.out.println("------> " + method);
        Map<String, Object> resp =
                (Map) connection.call("Service." + method, data.getData());
        
        Date currentTime = new Date();
        
        long deltaTime = currentTime.getTime() - lastTime.getTime();
        connection.close();
        
        return new Responder((Map) resp.get("data"), deltaTime);
    }
    
    public static void callAsync(final String url, final String method, final IParams data, final Handler handler)
    {
        new Thread()
        {
            @Override
            public void run() {
                try {
                    Responder resp = call(url, method, data);
                    handler.handle(resp);
                } catch (ClientStatusException | ServerStatusException ex) {
                    Logger.getLogger(AMFConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   
        }.start();
    }
}
