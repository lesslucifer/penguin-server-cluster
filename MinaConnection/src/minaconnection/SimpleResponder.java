/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoHandler;
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
    
    public SimpleResponder(int port, IoHandler handler) throws IOException {
        
        this.acceptor = new NioSocketAcceptor();
        
        this.acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter( new ObjectSerializationCodecFactory()));
        this.acceptor.getFilterChain().addLast("executor", new ExecutorFilter());
        
        acceptor.setHandler(handler);

        acceptor.bind(new InetSocketAddress(port));
    }
}
