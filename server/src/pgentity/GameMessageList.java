/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity;

import db.PGKeys;
import db.RedisKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import pgentity.redis.PGRedisSetEntity;

/**
 *
 * @author Salm
 */
public class GameMessageList extends PGRedisSetEntity {
    private GameMessageList() {}
    private static final GameMessageList inst = new GameMessageList();
    
    @Override
    protected RedisKey redisKey() {
        return PGKeys.GAMEMESSAGES;
    }
    
    public static GameMessageList getMessages()
    {
        return inst;
    }
    
    @Override
    public Set<String> getAll() {
        Set<String> expiredMsgs = new HashSet();
        Set<String> msgIDs = super.getAll();
        Set<String> availMsgs = new HashSet(msgIDs.size());
        
        for (String msgID : msgIDs) {
            if (GameMessage.isExist(msgID))
            {
                availMsgs.add(msgID);
            }
            else
            {
                expiredMsgs.add(msgID);
            }
        }
        
        if (expiredMsgs.size() > 0)
        {
            this.remove(expiredMsgs.toArray(new String[msgIDs.size()]));
        }
        
        return availMsgs;
    }
    
    private Set<GameMessage> getEnabledMsgs()
    {
        Set<String> msgIDs = this.getAll();
        Set<GameMessage> msgs = new HashSet(msgIDs.size());
        for (String msgID : msgIDs) {
            GameMessage msg = GameMessage.getMsg(msgID);
            if (msg.isEnable())
            {
                msgs.add(msg);
            }
        }
        
        return msgs;
    }
    
    private Set<GameMessage> getAllMsgs()
    {
        Set<String> msgIDs = this.getAll();
        Set<GameMessage> msgs = new HashSet(msgIDs.size());
        for (String msgID : msgIDs) {
            msgs.add(GameMessage.getMsg(msgID));
        }
        
        return msgs;
    }
    
    public Map<String, Object> buildAMF(boolean filtEnabled)
    {
        Set<GameMessage> allMsgs = getEnabledMsgs();
        
        Map<String, Object> msgAMFs = new HashMap(allMsgs.size());
        for (GameMessage msg : allMsgs) {
            msgAMFs.put(msg.getMsgID(), msg.buildAMF());
        }
        
        return msgAMFs;
    }
}