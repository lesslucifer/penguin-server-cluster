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
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONValue;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.redis.PGRedisListEntity;
import pgentity.redis.PGRedisSetEntity;
import share.AMFBuilder;
import share.PGMacro;

/**
 *
 * @author Salm
 */
@EntityFactory(factorier = "loadNotification")
public class Notification extends PGRedisListEntity {
    private final RedisKey redisKey;

    private Notification(String uid) {
        this.redisKey = redisKey(uid);
    }
    
    public static Notification getNotif(String uid)
    {
        return EntityPool.inst().get(Notification.class, uid);
    }
    
    private static Notification loadNotification(String uid)
    {
        return new Notification(uid);
    }
    
    public static RedisKey redisKey(String uid)
    {
        return PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_NOTIF);
    }
    
    public static void destroy(String uid)
    {
        DBContext.Redis().del(redisKey(uid));
    }
    
    @Override
    protected RedisKey redisKey() {
        return redisKey;
    }
    
    public Map<String, Object> takeAllNotifications()
    {
        List<String> allNotif = this.getAll();
        this.removeAll();
        Map<String, Object> notifs = new HashMap(allNotif.size());
        int i = 0;
        for (String notif : allNotif) {
            notifs.put(String.valueOf(i++), JSONValue.parse(notif));
        }
        
        return notifs;
    }
    
    public void send(String type, Object data)
    {
        this.push(JSONValue.toJSONString(AMFBuilder.make(
                "type", type,
                PGMacro.DATA, data)));
    }
}
