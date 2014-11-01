/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.events.release_event;

import db.DBContext;
import db.RedisKey;
import java.util.HashMap;
import java.util.Map;
import pgentity.PGEntity;
import pgentity.Penguin;
import share.AMFBuilder;
import share.PGHelper;
import share.PGMacro;

/**
 *
 * @author Salm
 */
class REPenguin implements PGEntity {
    private final Penguin penguin;
    private RedisKey redisKey;
    
    private long lastSpawnTime;

    private REPenguin(Penguin penguin) {
        this.penguin = penguin;
        this.redisKey = key(penguin.getUid(), penguin.getCoteID(),
                penguin.getPenguinID());
    }
    public static REPenguin getPenguin(Penguin penguin)
    {
        REPenguin rePenguin = new REPenguin(penguin);
        rePenguin.updateFromDB();
        
        return rePenguin;
    }
    
    public static RedisKey key(String uid, String coteID, String pID)
    {
        return Keys.USER.getChild(uid).getChild(coteID).getChild(pID);
    }

    /**
     * @return the penguin
     */
    public Penguin getPenguin() {
        return penguin;
    }

    /**
     * @return the redisKey
     */
    public RedisKey getRedisKey() {
        return redisKey;
    }

    /**
     * @param redisKey the redisKey to set
     */
    public void setRedisKey(RedisKey redisKey) {
        this.redisKey = redisKey;
    }

    /**
     * @return the lastSpawnItem
     */
    public long getLastSpawnTime() {
        return lastSpawnTime;
    }

    /**
     * @param lastSpawnItem the lastSpawnItem to set
     */
    public void setLastSpawnTime(long lastSpawnItem) {
        this.lastSpawnTime = lastSpawnItem;
    }

    @Override
    public void updateFromDB() {
        Map<String, String> data = DBContext.Redis().hgetall(redisKey);
        
        this.lastSpawnTime = PGHelper.toLong(data.get(PGMacro.TIME_LAST_SPAWN));
    }

    @Override
    public void saveToDB() {
        Map<String, String> data = new HashMap(1);
        
        data.put(PGMacro.TIME_LAST_SPAWN, String.valueOf(this.lastSpawnTime));
        DBContext.Redis().hset(redisKey, data);
    }
    
    public Object buildAMF()
    {
        return AMFBuilder.make(PGMacro.TIME_LAST_SPAWN, this.lastSpawnTime);
    }
}
