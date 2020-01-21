/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amfclient.action;

import com.mycompany.amfclient.PGTestLog;
import com.mycompany.amfclient.Responder;
import com.mycompany.amfclient.amfconnection.AMFConnection;
import com.mycompany.amfclient.amfparams.PGParamsCreator;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import flex.messaging.io.amf.client.exceptions.ServerStatusException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author suaongmattroi
 */
public class AcAuthenticate {
    
    public void authenticateSystem(String uid, Consumer<String> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("uid", uid);
        data.put("signed_request", "khong1sau9bon6chin3@TAM@");
        
        Responder resp = AMFConnection.call("authenticateSystem", 
                PGParamsCreator.getInst().create(uid, data, null));
        String sid = (String) resp.getParams().get("sid");
        
        PGTestLog.getInst().info("Authenticate complete");
        
        callback.accept(sid);
    }
    
    public void authenticate(String uid, Consumer<String> callback)
            throws ClientStatusException, ServerStatusException
    {
        authenticateSystem(uid, callback);
        
//        Map<String, Object> data = new HashMap();
//        String sid = "1F0189889CC54634869E0D9F";
//        data.put("uid", uid);
//        data.put("sid", sid);
//        data.put("signed_request", "EaxW4N7tUhUWPfCblkcN8BQPWZtBPVeOAw3NFE8Y1HM=.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjcyMDAsImlzc3VlZF9hdCI6MTQxNDkwNjc3MywiYWNjZXNzX3Rva2VuIjoiY2NjMWM2OTdjZDI4NDcyNmEyM2ZhMjg4ODRjZWVkNGEuWkRZd05qTXlZV1k9bDhxOGJxMGpLNzYta2ROTkFvOURURDN3RXhQSjYyeVRuQ1RUV2F2dUgyVlBoTVk0NmQ1QjZmM0VNd09BR2JhVnlDcjYtc1NBTkd0ZXMxbzFGNzliSEQyS05ESmF6VzBHOVhiNXJjZURJWjdGdVdZY0gwalBQaUVaUVE4STZZRGNkbGlSd0s1VFMyWlp5cmtVSDZic0FGQktOQzhfWEZPYk1jYnhNbT09IiwidWlkIjoyMzg4NTcxN30=&code=6JPA0BfqFqvv05OStM5GPoTMBsA7R7GMSLbsQQDZNq8YDH98wmmRO1Lf5n6RGLDT5Y18ARSI5mmLUn4ve7emG056FpUW9JSGKqqxFgeZOXWB73mwf7an6JD00J6CCKzJ74XJ5Rmd73fAAb5rYbq8LZrIP4J-B0fLFWSp1Q5GdOPD9ld1odRyii8Taw5NGuVbWXYQkcGLWVgj5fZh40M4llS9khap9iVU_5wccracRepnRO1YVni=");
//        
//        Responder resp = AMFConnection.call("authenticate", 
//                PGParamsCreator.getInst().create(uid, data, null));
//        
//        PGTestLog.getInst().info("Authenticate complete");
//        
//        callback.accept(sid);
    }
}
