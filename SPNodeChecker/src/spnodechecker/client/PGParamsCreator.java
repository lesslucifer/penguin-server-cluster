/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spnodechecker.client;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author suaongmattroi
 */
public class PGParamsCreator {
    
    private static final PGParamsCreator inst;
    
    static 
    {
        inst = new PGParamsCreator();
    }
    
    public static PGParamsCreator getInst()
    {
        return inst;
    }
    
    public IParams create(String uid, Map<String, Object> data, Map<String, String> sessions)
    {
        return new PGParams(uid, data, sessions);
    }
    
    class PGParams implements IParams
    {
        private final String uid;
        private final Map<String, Object> data;
        private final Map<String, String> sessions;
        public PGParams(String uid, Map<String, Object> data, Map<String, String> sessions)
        {
            this.uid = uid;
            this.data = data;
            this.sessions = sessions;
        }
        
        @Override
        public Map<String, Object> getData() {
            Map<String, Object> params = new HashMap();
        
            params.put("data", data);

            Map<String, Object> cheat = new HashMap();
            cheat.put("uid", uid);
            params.put("cheat", cheat);

            Map<String, Object> session = new HashMap();
            session.put("uid", uid);
            if(sessions != null)
                session.put("sid", sessions.get(uid));
            params.put("session", session);

            Map<String, Object> config = new HashMap();
            config.put("version", "1.0");
            params.put("config", config);

            return params;
        }
    }
}
