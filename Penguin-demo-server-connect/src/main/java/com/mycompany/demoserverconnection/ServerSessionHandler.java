/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.demoserverconnection;

import java.lang.reflect.Method;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author hieubui
 */
public class ServerSessionHandler extends IoHandlerAdapter{
    
    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
        cause.printStackTrace();
    }
    
    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception
    {
        String str = message.toString();
        if( str.trim().equalsIgnoreCase("quit") ) {
            session.close();
            return;
        }
        System.out.println("Server recieve message: " + str);
//        if(message instanceof myObject)
//        {
//            Method method = message.getClass().getMethod(str);
//            String param = (String)method.invoke(message);
//        
//            System.out.println("Server recieve message: " + param);
//        }
    }
    
     @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception
    {
        //System.out.println( "IDLE " + session.getIdleCount( status ));
    }
}
