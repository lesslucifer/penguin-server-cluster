/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package connection;

import connection.data.interfaces.IPGData;
import connection.data.interfaces.IServices;
import connection.handler.PGRouter;
import connection.handler.SimpleIoHandler;
import com.penguin.test.TestResponseder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.Map;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 *
 * @author suaongmattroi
 */
public class SimpleResponder {
    
    private final NioSocketAcceptor acceptor;
    
    public SimpleResponder(int port, SimpleIoHandler handler) throws IOException {
        
        this.acceptor = new NioSocketAcceptor();
        
        Map<String, IoFilterAdapter> filters = new Hashtable<>();
        filters.put("codec", new ProtocolCodecFilter( new ObjectSerializationCodecFactory()));
        
        // Apply filter to connection
        for(Map.Entry<String, IoFilterAdapter> entry : filters.entrySet()) {   
            this.acceptor.getFilterChain().addLast(entry.getKey(), entry.getValue() );
        }
        acceptor.setHandler(handler);

        acceptor.bind(new InetSocketAddress(port));
    }
    
    public static void main(String[] args) throws IOException {
        
        // Create services
        IServices services = new TestResponseder();
        
        // Create router
        final PGRouter router = new PGRouter(services);
        
        SimpleResponder req = new SimpleResponder(18567,
            new SimpleIoHandler()
            {
                @Override
                public void messageReceived(IoSession session, Object message) throws Exception {
                    if(router != null) {
                        IPGData data = (IPGData) message;
                        String method = data.getMethod();
                        router.drive(method, session, data);
                        System.out.println("Handling " + method + "...");
                    }
                }
            });
        System.out.println("Server started to listen...");
    }
}
