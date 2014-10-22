/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import minaconnection.interfaces.IClientHandler;
import minaconnection.interfaces.IRequestPool;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;

/**
 *
 * @author suaongmattroi
 */
class SimpleRequestPool implements IRequestPool{
    
    private final int MAX_CONNECTION = 1000;
    
    private final AtomicLong _connectionCounter;
    
    private final Map<MinaAddress, SimpleRequester> _banks;
    private final Map<Long, IClientHandler> _callers;
    
    public SimpleRequestPool() {
        _connectionCounter = new AtomicLong(0);
        _banks = new ConcurrentHashMap();
        _callers = new ConcurrentHashMap();
    }
    
    private long nextIndex() {
        return _connectionCounter.incrementAndGet();
    }
    
    @Override
    public void request(MinaAddress address, Serializable data, IClientHandler h) {
        
        if(!_banks.containsKey(address))
        {
            createConnection(address);
        }
        
        long index = nextIndex();
        IMinaData mData = new MinaData(index, data);
        
        _callers.put(index, h);
        
        SimpleRequester sRequest = _banks.get(address);
        sRequest.send(mData);
    }
    
    private void createConnection(MinaAddress address) 
    {    
        // [LOG HERE]
        System.out.println("Create new connection at: " + address.getAddress() + ", port: " + Integer.toString(address.getPort()));
        
        Map<String, IoFilterAdapter> filters = new ConcurrentHashMap();
        filters.put("codec", new ProtocolCodecFilter( new ObjectSerializationCodecFactory()));
        filters.put("executor", new ExecutorFilter());
        
        SimpleRequester req = new SimpleRequester(address, filters, 
            new MinaSimpleIoHandler() {
            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                handleReceived(session, message);
            }
        });
        req.start();
        _banks.put(address, req);
    }
    
    private void handleReceived(IoSession session, Object message) throws Exception 
    {
        IMinaData data = (IMinaData) message;
        Long index = data.index();
        if(_callers.containsKey(index)) 
        {
            IClientHandler caller = _callers.get(index);
            _callers.remove(index);
            
            reflect(caller, data);
        }
    }
    
    private void reflect(IClientHandler caller, IMinaData data) 
            throws NoSuchMethodException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException 
    {    
        caller.callback(data.data());
    }
}
