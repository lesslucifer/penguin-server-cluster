/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.events.release_event;

import db.RedisKey;
import pgentity.CheckerLogPool;
import pgentity.LogPool;
import pgentity.redis.PGRedisSetEntity;

/**
 *
 * @author Salm
 */
class UserEvents extends PGRedisSetEntity {
    private final String uid;
    private final RedisKey redisKey;

    private UserEvents(String uid) {
        this.uid = uid;
        redisKey = Keys.USER.getChild(uid).getChild(Keys.FD_EVENT);
    }
    public static UserEvents getEvents(String uid)
    {
        return new UserEvents(uid);
    }
    
    @Override
    protected RedisKey redisKey() {
        return redisKey;
    }

    public String getUid() {
        return uid;
    }
    
    public LogPool getLogPool()
    {
        return LogPool.getPool(redisKey);
    }
    
    public CheckerLogPool getCheckerLogPool()
    {
        return CheckerLogPool.getPool(redisKey);
    }
}
