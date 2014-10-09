/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import minaconnection.PGAddress;
import minaconnection.PGAddress;
import minaconnection.SimpleRequester;
import minaconnection.SimpleRequester;
import minaconnection.handler.SimpleIoHandler;
import minaconnection.interfaces.IPGData;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;

/**
 *
 * @author suaongmattroi
 */
public class SimpleRequestPool {
    
    private final int MAX_CONNECTION = 1000;
    
    private final AtomicLong _connectionCounter;
    
    private final Map<PGAddress, SimpleRequester> _banks;
    private final Map<Long, PGCaller> _callers;
    
    private class PGCaller  {
        private final Object _caller;

        private final String _callBack;
        
        public PGCaller(Object caller, String callBack)
        {
            _caller = caller;
            _callBack = callBack;
        }
        
        public Object getCaller() {
            return _caller;
        }

        public String getCallBack() {
            return _callBack;
        }
    }
    
    public SimpleRequestPool() {
        _connectionCounter = new AtomicLong(0);
        _banks = new ConcurrentHashMap<PGAddress, SimpleRequester>();
        _callers = new ConcurrentHashMap<Long, PGCaller>();
    }
    
    public long getIndex() {
        return _connectionCounter.addAndGet(1);
    }
    
    public void request(PGAddress address, IPGData data, String callBack, Object caller) {
        
        if(!_banks.containsKey(address))
        {
            createConnection(address);
        }
        
        _callers.put(data.getIndex(), new PGCaller(caller, callBack));
        SimpleRequester sRequest = _banks.get(address);
        sRequest.send(data);
    }
    
    private void createConnection(PGAddress address) {
        
        // Log here
        System.out.println("Create new connection at: " + address.getAddress() + ", port: " + Integer.toString(address.getPort()));
        
        Map<String, IoFilterAdapter> filters = new ConcurrentHashMap<String, IoFilterAdapter>();
        filters.put("codec", new ProtocolCodecFilter( new ObjectSerializationCodecFactory()));
        
        SimpleRequester req = new SimpleRequester(address, filters, 
            new SimpleIoHandler()
            {
                @Override
                public void messageReceived(IoSession session, Object message) throws Exception {
                    handleReceived(session, message);
                }
                
                @Override
                public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                    super.exceptionCaught(session, cause);
                }
            });
        req.start();
        _banks.put(address, req);
    }
    
    private void handleReceived(IoSession session, Object message) throws Exception {
        IPGData data = (IPGData) message;
        Long index = data.getIndex();
        if(_callers.containsKey(index)) {
            
            PGCaller caller = _callers.get(index);
            _callers.remove(index);
            
            reflect(caller, data);
        }
    }
    
    private void reflect(PGCaller caller, IPGData data) 
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        
        Method m = caller.getCaller().getClass().getMethod(caller.getCallBack(), IPGData.class);
        m.invoke(caller.getCaller(), data);
    }
}
