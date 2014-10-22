/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import minaconnection.interfaces.IMinaData;
import minaconnection.interfaces.IServerHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 *
 * @author suaongmattroi
 */
public class SimpleResponder {
    
    private final NioSocketAcceptor acceptor;
    
    private final IServerHandler handler;
    
    public SimpleResponder(int port, IServerHandler h) throws IOException {
        
        this.acceptor = new NioSocketAcceptor();
        
        this.acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter( new ObjectSerializationCodecFactory()));
        this.acceptor.getFilterChain().addLast("executor", new ExecutorFilter());
        
        this.handler = h;
        this.acceptor.setHandler(new MinaSimpleIoHandler()
        {
            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                IMinaData mData = (IMinaData) message;
                handler.messageReceived(session, mData.index(), mData.data());
            }

            @Override
            public void messageSent(IoSession session, Object message) throws Exception {
                IMinaData mData = (IMinaData) message;
                handler.messageSent(session, mData.index(), mData.data());
            }
            
            @Override
            public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                handler.exceptionCaught(session, cause);
            }
        });

        acceptor.bind(new InetSocketAddress(port));
    }
    
    public void send(IoSession session, long index, Serializable obj)
    {
        IMinaData mData = new MinaData(index, obj);
        session.write(mData);
    }
}