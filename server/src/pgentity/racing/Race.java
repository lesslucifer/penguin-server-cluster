/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.racing;

import db.DBContext;
import db.PGKeys;
import db.RedisKey;
import java.util.HashMap;
import java.util.Map;
import pgentity.PGEntity;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import share.PGError;
import share.PGException;
import share.PGHelper;
import share.PGMacro;

/**
 *
 * @author Salm
 */
@EntityFactory(factorier = "loadRace")
public class Race implements PGEntity {
    private final RedisKey redisKey;
    private final String token;
    private String name;
    private long start;
    private long end;
    private String criteria;
    private boolean isClosed;
    
    private Race(String token)
    {
        this.token = token;
        redisKey = redisKey(token);
    }
    
    public static Race getRace(String token)
    {
        return EntityPool.inst().get(Race.class, token);
    }
    
    private static Race loadRace(String token)
    {
        PGException.Assert(DBContext.Redis().exists(redisKey(token)),
                PGError.INVALID_RACE, token);
        
        Race race = new Race(token);
        race.updateFromDB();
        
        return race;
    }
    
    public static Race newRace(String token, String name,
            long start, long end, String criteria)
    {
        PGException.Assert(!DBContext.Redis().exists(redisKey(token)),
                PGError.INVALID_RACE, "Race " + token + " already existed");
        
        Race race = new Race(token);
        race.name = name;
        race.start = start;
        race.end = end;
        race.criteria = criteria;
        race.isClosed = false;
        race.saveToDB();
        
        EntityPool.inst().put(race, Race.class, token);
        
        return race;
    }
    
    public static RedisKey redisKey(String token)
    {
        return PGKeys.RACING.getChild(token);
    }
    
    @Override
    public void updateFromDB() {
        Map<String, String> data = DBContext.Redis().hmget(redisKey);
        name = data.get(PGMacro.NAME);
        start = PGHelper.toLong(data.get(PGMacro.RACING_START));
        end = PGHelper.toLong(data.get(PGMacro.RACING_END));
        criteria = data.get(PGMacro.RACING_CRITERIA);
        isClosed = PGHelper.toBoolean(PGMacro.RACING_CLOSED);
    }

    @Override
    public void saveToDB() {
        Map<String, String> data = new HashMap(3);
        
        data.put(PGMacro.NAME, name);
        data.put(PGMacro.RACING_START, String.valueOf(start));
        data.put(PGMacro.RACING_END, String.valueOf(end));
        data.put(PGMacro.RACING_CRITERIA, String.valueOf(criteria));
        data.put(PGMacro.RACING_CLOSED, String.valueOf(isClosed));
        DBContext.Redis().hset(redisKey, data);
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public String getCriteria() {
        return criteria;
    }
    
    public boolean isClosed()
    {
        return this.isClosed;
    }
    
    public void close()
    {
        isClosed = true;
    }
}
