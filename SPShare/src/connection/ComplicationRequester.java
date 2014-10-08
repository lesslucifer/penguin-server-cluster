/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package connection;

import connection.data.interfaces.IPGData;
import connection.event.INotifable;
import connection.event.NotifableCounter;
import connection.handler.MultipleIoHandler;
import java.util.Hashtable;
import java.util.Map;
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
    
    public void callBack(){
        Map<String, INotifable> results = this.notifableCounter.getNotifables();
        for(Map.Entry<String, INotifable> entry : results.entrySet()) {
            String key = entry.getKey();
            MultipleIoHandler handler = (MultipleIoHandler) entry.getValue();
            IPGData data = (IPGData) handler.getParams();
            System.out.println(key + ":" + data.getData().toString());
        }
    }
    
    public static void main(String[] args) {
        Map<String, IoFilterAdapter> filters = new Hashtable<>();
        filters.put("codec", new ProtocolCodecFilter( new ObjectSerializationCodecFactory()));
        
        ComplicationRequester cRequest = new ComplicationRequester();
        cRequest.addOwner(cRequest, "callBack");
        
        SimpleRequester friendlistReq = cRequest.addRequest(new PGAddress("localhost", 18567), filters, "friendlist");
        SimpleRequester loadGameReq = cRequest.addRequest(new PGAddress("localhost", 18567), filters, "loadGame");
        SimpleRequester dataUserReq = cRequest.addRequest(new PGAddress("localhost", 18567), filters, "dataUser");
        
//        friendlistReq.send(new PGStringData(0, "getFriendlist", "14992"));
//        loadGameReq.send(new PGStringData(1, "loadGame", "14992"));
//        dataUserReq.send(new PGStringData(2, "getDataUser", "14992"));
    }
}
