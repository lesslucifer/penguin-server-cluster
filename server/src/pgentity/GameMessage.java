/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import db.PGKeys;
import db.RedisKey;
import java.util.HashMap;
import java.util.Map;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import share.PGError;
import share.PGException;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadMsg")
public class GameMessage implements PGEntity {
    
    private final String msgID;
    private String content;
    private int order;
    private boolean enable = true;
    
    private GameMessage(String msgID) {
        this.msgID = msgID;
    }
    
    public static GameMessage getMsg(String msgID)
    {
        return EntityPool.inst().get(GameMessage.class, msgID);
    }
    
    private static GameMessage loadMsg(String msgID)
    {
        PGException.Assert(DBContext.Redis().exists(redisKey(msgID)),
                PGError.UNDEFINED,
                "Game message: " + msgID + " not exist");
        
        GameMessage msg = new GameMessage(msgID);
        msg.updateFromDB();
        
        return msg;
    }
    
    public static GameMessage newMsg(String msgID,
            String content, int order, int expire)
    {
        GameMessage msg = new GameMessage(msgID);
        msg.content = content;
        msg.order = order;
        
        msg.saveToDB();
        DBContext.Redis().expire(redisKey(msgID), expire);
        EntityPool.inst().put(msg, GameMessage.class, msgID);
        
        return msg;
    }
    
    public static RedisKey redisKey(String msgID)
    {
        return PGKeys.GAMEMESSAGES.getChild(msgID);
    }
    
    public static void destroyMsg(String msgID)
    {
        DBContext.Redis().del(redisKey(msgID));
        EntityPool.inst().remove(GameMessage.class, msgID);
    }
    
    public static boolean isExist(String msgID)
    {
        return DBContext.Redis().exists(redisKey(msgID));
    }
    
    @Override
    public void updateFromDB()
    {
        Map<String, String> data = DBContext.Redis().hgetall(redisKey(msgID));
        
        this.content = data.get("content");
        this.order = PGHelper.toInteger(data.get("order"));
        this.enable = PGHelper.toBoolean(data.get("enable"));
    }

    @Override
    public void saveToDB() {
        Map<String, String> data = new HashMap(2);
        
        data.put("content", content);
        data.put("order", String.valueOf(order));
        data.put("enable", String.valueOf(enable));
        
        DBContext.Redis().hset(redisKey(msgID), data);
    }

    public String getMsgID() {
        return msgID;
    }

    public String getContent() {
        return content;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Map<String, Object> buildAMF()
    {
        Map<String, Object> data = new HashMap();
        
        data.put("msgID", this.msgID);
        data.put("content", content);
        data.put("order", this.order);
        
        return data;
    }
}
