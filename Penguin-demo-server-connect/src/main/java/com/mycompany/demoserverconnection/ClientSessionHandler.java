/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.demoserverconnection;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * {@link IoHandler} for SumUp client.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (금, 13  7월 2007) $
 */
public class ClientSessionHandler extends IoHandlerAdapter {
    
    IoSession session;

    public ClientSessionHandler(int[] values) {
    }

    @Override
    public void sessionOpened(IoSession session) {
        
        this.session = session;
//        
//        Map<String, myObject> objs = new Hashtable<>();
//        for(int i = 0; i < 100; ++i)
//        {
//            myObject mObj = new myObject(Integer.toString(i), i);
//            objs.put(Integer.toString(i), mObj);
//        }
//        
//        session.write(objs);
//        
//        System.out.println("session is opened");
    }
    
    @Override
    public void messageReceived(IoSession session, Object message) {
       
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        session.close();
    }
    
    public void send(SerializeObject obj)
    {
        if(obj != null && session.isConnected())
        {
            session.write(obj);
        }
    }
    
}