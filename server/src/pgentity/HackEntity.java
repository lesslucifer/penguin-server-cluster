/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.HashMap;
import java.util.Map;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import db.RedisKey;

/**
 * Some hacking data for test
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadEntity")
public class HackEntity implements PGEntity
{
    private static final RedisKey HACK = RedisKey.root().getChild("hack");
    
    private final String uid;
    private long deltaTime;
    
    private HackEntity(String uid)
    {
        this.uid = uid;
    }
    
    /**
     * Static factory for hack entity
     * If user haven't been hacked, this method will
     * set user is hacked (ref <i>isHacked</i> method) automatically
     * @param uid
     * @return
     */
    public static HackEntity getEntity(String uid)
    {
        return EntityPool.inst().get(HackEntity.class, uid);
    }
    
    private static HackEntity loadEntity(String uid)
    {
        HackEntity hacker = new HackEntity(uid);
        if (makeSureUserExist(hacker))
        {
            hacker.updateFromDB();
        }
        
        return hacker;
    }
    
    private static boolean makeSureUserExist(HackEntity hacker)
    {
        return DBContext.Redis().exists(HACK.getChild(hacker.getUid()));
    }
    
    private RedisKey redisKey()
    {
        return HACK.getChild(this.uid);
    }

    @Override
    public void updateFromDB()
    {
        this.deltaTime = Long.valueOf(DBContext.Redis().hget(this.redisKey(), "time"));
    }

    @Override
    public void saveToDB()
    {
        Map<String, String> data = new HashMap();
        data.put("time", String.valueOf(this.deltaTime));
        
        DBContext.Redis().hset(this.redisKey(), data);
    }
    
    /**
     * 
     */
    public void destroy()
    {
        DBContext.Redis().srem(HACK, uid);
        DBContext.Redis().del(this.redisKey());
    }

    public long getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime(long deltaTime) {
        this.deltaTime = deltaTime;
    }

    public String getUid() {
        return uid;
    }
}
