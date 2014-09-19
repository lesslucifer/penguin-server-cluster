/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.demoserverconnection;

import java.net.InetSocketAddress;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 *
 * @author hieubui
 */
public class Reciever {
    
    private String _address;
    private int _port;
    
    private IoHandlerAdapter handler;
    
    public Reciever(String address, int port)
    {
        _address = address;
        _port = port;
        
        System.out.println("Client started");
    }
    
    public void connect()
    {
        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(10000);

        connector.getFilterChain().addLast("codec",
            new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

        connector.getFilterChain().addLast("logger", new LoggingFilter());
        
        int[] values = new int[10];
        for(int i = 0; i < 10; ++i)
        {
            values[i] = i;
        }
        
        handler = new ClientSessionHandler(values);
        connector.setHandler(handler);
        IoSession session;

        for (;;) {
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(_address, _port));
                future.awaitUninterruptibly();
                session = future.getSession();
                break;
            } catch (RuntimeIoException e) {
                System.err.println("Fail to connect");
                e.printStackTrace();
            }
        }
        
        session.getCloseFuture().awaitUninterruptibly();
        connector.dispose();
    }
    
    public void write(SerializeObject obj)
    {
        if(handler instanceof ClientSessionHandler)
            ((ClientSessionHandler)handler).send(obj);
    }
}
