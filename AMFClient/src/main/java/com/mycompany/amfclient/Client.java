/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.amfclient;

import com.mycompany.amfclient.Utils.Random;
import edu.emory.mathcs.backport.java.util.Collections;
import flex.messaging.io.amf.client.AMFConnection;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import flex.messaging.io.amf.client.exceptions.ServerStatusException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KieuAnh
 */
public class Client {
    static Map<String, String> sessions = new ConcurrentHashMap<>();
    static Map<String, StringBuilder> logs = new ConcurrentHashMap<>();
    private static final int NUSERS = 1000;
    private static final int NACTIVE = 100;
    private static final int MAX_FRIENDS_VISIT = 100;
    
    private static final Set<String> remainUsers = Collections.synchronizedSet(new HashSet<String>());
    
    public static void main(String []args) throws ClientStatusException, InterruptedException
    {
//        List<Integer> uids = Random.randomInRange(NUSERS, 0, NACTIVE);
//        for(Integer uid : uids)
//        {
//            remainUsers.add("TESTUSER" + uid.toString());
//        }
//        
//        Set<String> allUsers = new HashSet(remainUsers);
//        for (String uid : allUsers) {
//            callLoadUser(uid);
//            pause();
//        }
    }
    
    private static void completeUser(String uid)
    {
//        remainUsers.remove(uid);
//        if(remainUsers.isEmpty())
//        try {
//            saveLog();
//        } catch (IOException ex) {
//            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
    
//    private static void callLoadUser(String uid) throws ClientStatusException
//    {
//        Thread aClient = new Thread()
//        {
//            private String uid;
//            private Map<Integer, String> friends;
//
//            public Thread init(String uid) throws ClientStatusException
//            {
//                this.uid = uid;
//                StringBuilder strBuilder = new StringBuilder("");
//                logs.put(uid, strBuilder);
//                return this;
//            }
//
//            @Override
//            public void run() {
//                try
//                {
//                    authenticate(uid);
//                    ============================
//                 
//                    
//                    ============================
//                    callAsync(uid, "getFriendList", loadFriendListParams(), new Handler()
//                    {
//                        @Override
//                        public void handle(Responder dataResp) {
//                            if(dataResp.getParams() != null)
//                            {
//                                Client.createDataLog(uid, "getFriendList", dataResp.getTime(), true);
//                                
//                                // Get list friend
//                                int nFriendVisited = 0;
//                                for(Map.Entry<String, Object> eachFriend : dataResp.getParams().entrySet())
//                                {
//                                    if(nFriendVisited >= MAX_FRIENDS_VISIT)
//                                    {
//                                        break;
//                                    }
//                                    
//                                    ++nFriendVisited;
//                                    try {
//                                        visitAndStealFriends(eachFriend.getKey());
//                                    } catch (ClientStatusException | ServerStatusException ex) {
//                                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//                                    }
//                                    pause();
//                                }
//                            }
//                            else
//                            {
//                                Client.createDataLog(uid, "getFriendList", dataResp.getTime(), false);
//                            }
//                            
//                            completeUser(uid);
//                        }
//                    });
//                }
//                catch (ClientStatusException | ServerStatusException ex) {
//                    System.err.println(ex.getMessage());
//                }
//            }
//            
//            private void visitAndStealFriends(final String fid) throws ClientStatusException, ServerStatusException
//            {
//                Responder dataResp = call(uid, "visitFriend", loadDataFriendParams(fid));
//                if(dataResp.getParams() != null)
//                {
//                    createDataLog(uid, "visitFriend", dataResp.getTime(), true);
//
//                    Map<String, Object> currentCote = (Map)dataResp.getParams().get("friend");
//                    String coteId = (String)currentCote.get("cote_id");
//
//                    pause();
//                    stealFriend(fid, coteId);
//                }
//                else
//                {
//                    createDataLog(uid, "visitFriend", dataResp.getTime(), false);
//                }
//            }
            
//            private void stealFriend(String fid, String coteId) throws ClientStatusException, ServerStatusException
//            {
//                Responder dataResp = call(uid, "stealEgg", stealFriendParams(fid, coteId, "Egg_0"));
//                if(dataResp.getParams() != null)
//                {
//                    createDataLog(uid, "stealEgg", dataResp.getTime(), true);
//                }
//                else
//                {
//                    createDataLog(uid, "stealEgg", dataResp.getTime(), false);
//                }
//
//                pause();
//            }
//        }.init(uid);
//        aClient.start();
//        }
//    }
    
//    private static void pause()
//    {
//        try {
//            Thread.sleep(Random.randomMiliseconds());
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
//    private static Map<String, Object> loadGameParams(String uid)
//            throws ClientStatusException, ServerStatusException
//    {
//        Map<String, Object> data = new HashMap();
//        data.put("uid", uid);
//        data.put("signed_request", "1@3$5^bayTAM");
//        
//        return data;
//    }
//    
//    private static Map<String, Object> loadFriendListParams()
//    {
//        Map<String, Object> data = new HashMap();
//        
//        return data;
//    }
//    
//    private static Map<String, Object> loadDataFriendParams(String fid)
//    {
//        Map<String, Object> data = new HashMap();
//        data.put("friend_id", fid);
//        
//        return data;
//    }
//    
//    private static Map<String, Object> stealFriendParams(String fid, String cid, String kind)
//    {
//        Map<String, Object> data = new HashMap();
//        data.put("friend_id", fid);
//        data.put("cote_id", cid);
//        data.put("kind", kind);
//        
//        return data;
//    }
//    
//    private static String createDataLog(String uid, String service, long time, boolean result)
//    {
//        Date currentDate = new Date();
//				
//        StringBuilder data = new StringBuilder();
//        data.append(currentDate.getMinutes()).append(":").append(currentDate.getSeconds()).append("\t");
//        data.append(uid).append("\t");
//        data.append(service).append("\t");
//        data.append(time).append("\t");
//        data.append((result) ? "Success" : "Failed").append("\n");
//        
//        String str = data.toString();
//        
//        StringBuilder logData = (StringBuilder)logs.get(uid);
//        logData.append(data.toString());
//        
//        return str;
//    }
//    
//    private static void saveLog() throws IOException
//    {
//        File file = new File("save.txt");
//        file.createNewFile();
//        
//        PrintStream ps = new PrintStream(file);
//        for(Map.Entry<String, StringBuilder> entry : logs.entrySet())
//        {
//            StringBuilder sb = entry.getValue();
//            ps.print(sb.toString());
//        }
//    }

