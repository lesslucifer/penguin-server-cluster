/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package connection;

import connection.PGAddress;
import connection.data.impl.PGStringData;
import connection.data.interfaces.IPGData;
import connection.handler.SimpleIoHandler;
import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.Map;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 *
 * @author suaongmattroi
 */
public class SimpleRequester {
    
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
    
    public void start() {

        ConnectFuture connFuture = 
                this.connector.connect(new InetSocketAddress(this.address.getAddress(), this.address.getPort()));
        connFuture.awaitUninterruptibly();

        this.session = connFuture.getSession();
    }
    
    public void send(IPGData req) {
        
        if(this.session != null && this.session.isConnected()) {
            this.session.write(req);
        }
    }
    
    public static void main(String[] args) {
        PGAddress address = new PGAddress("localhost", 18567);
        
        Map<String, IoFilterAdapter> filters = new Hashtable<>();
        filters.put("codec", new ProtocolCodecFilter( new ObjectSerializationCodecFactory()));
        
        SimpleRequester req = new SimpleRequester(address, filters, 
            new SimpleIoHandler()
            {
                @Override
                public void messageReceived(IoSession session, Object message) throws Exception {
                    IPGData respData = (IPGData) message;
                    System.out.println("Have received: " + respData.getData().toString());
                }
            });
        req.start();
        
        String strRequest = "I <3 U";
        System.out.println("Have requested: " + strRequest);
//        IPGData msg = new PGStringData(1, "Println", strRequest);
//        req.send(msg);
    }
}