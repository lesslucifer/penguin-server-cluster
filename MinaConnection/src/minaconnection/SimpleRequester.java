/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection;

import java.net.InetSocketAddress;
import java.util.Map;
import minaconnection.interfaces.IPGData;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 *
 * @author suaongmattroi
 */
public class SimpleRequester {
    
    private long timeout;
    
    private final IoConnector connector;
    
    private IoSession session;
    
    private final PGAddress address;
    
    public SimpleRequester(PGAddress address, Map<String, IoFilterAdapter> filters, IoHandlerAdapter handler) {
        
        this.address = address;
        
        this.connector = new NioSocketConnector();

        // Apply filter to connection
        for(Map.Entry<String, IoFilterAdapter> entry : filters.entrySet()) {   
            this.connector.getFilterChain().addLast(entry.getKey(), entry.getValue() );
        }
        this.connector.setHandler(handler);
    }
    
    public void setTimeOut(long timeout)
    {
        this.timeout = timeout;
        this.connector.setConnectTimeoutMillis(this.timeout);
    }
    
    public void start() {

        ConnectFuture connFuture = 
                this.connector.connect(new InetSocketAddress(this.address.getAddress(), this.address.getPort()));
        connFuture.awaitUninterruptibly();

        this.session = connFuture.getSession();
    }
    
    public boolean isAvailable() {
        return (this.session != null && this.session.isConnected());
    }
    
    public void send(IPGData req) {
        
        if(isAvailable()) {
            this.session.write(req);
        }
    }
}