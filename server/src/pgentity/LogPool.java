/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.HashSet;
import java.util.Set;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
public class LogPool
{
    private final int expireTime;
    private final RedisKey poolKey;
    private final Set<RedisKey> needExpires;

    private LogPool(RedisKey poolKey, int expire)
    {
        this.poolKey = poolKey;
        this.expireTime = expire;
        this.needExpires = (expire > 0)?(new HashSet()):null;
    }
    
    public static LogPool getPool(RedisKey key)
    {
        return new LogPool(key, 0);
    }
    
    public static LogPool getPool(RedisKey key, int expireInSecs)
    {
        return new LogPool(key, expireInSecs);
    }
    
    public RedisKey beginLog(String field)
    {
        RedisKey key = poolKey.getChild(field);
        beginCheckExpire(key);
        return key;
    }
    
    private void beginCheckExpire(RedisKey key)
    {
        if (expireTime > 0 && !needExpires.contains(key) &&
                !DBContext.Redis().exists(key))
        {
            needExpires.add(key);
        }
    }
    
    public void endLog(String field)
    {
        RedisKey key = poolKey.getChild(field);
        endCheckExpire(key);
    }
    
    private void endCheckExpire(RedisKey key)
    {
        if (expireTime > 0 && needExpires.contains(key))
        {
            DBContext.Redis().expire(key, expireTime);
            needExpires.remove(key);
        }
    }
}