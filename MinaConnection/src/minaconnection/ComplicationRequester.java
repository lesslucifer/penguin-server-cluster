/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection;

import java.util.Hashtable;
import java.util.Map;
import minaconnection.event.INotifable;
import minaconnection.event.NotifableCounter;
import minaconnection.handler.MultipleIoHandler;
import minaconnection.interfaces.IPGData;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;

/**
 *
 * @author suaongmattroi
 */
public class ComplicationRequester {
    
    private final NotifableCounter notifableCounter;
    
    private final Map<String, IPGData> results;
    
    public ComplicationRequester() {
        this.notifableCounter = new NotifableCounter();
        
        this.results = new Hashtable<String, IPGData>();
    }
    
    public void addOwner(Object owner, String callBack) {
        this.notifableCounter.register(this, callBack);
    }
    
    public SimpleRequester addRequest(PGAddress address, Map<String, IoFilterAdapter> filters, String identify) {
        
        MultipleIoHandler handler = new MultipleIoHandler();
        SimpleRequester req = new SimpleRequester(address, filters, handler);
        req.start();
        
        if(this.notifableCounter != null) {
            this.notifableCounter.addNotifable(handler, identify);
        }
        
        return req;
    }
}
