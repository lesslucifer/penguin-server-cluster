/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.redis.list;

import com.google.common.collect.Lists;
import db.DBContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import pgentity.PGEntity;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
public class RedisList implements PGEntity{
    /**
     * Provide an weak cache for Redis list
     */
    private final RedisKey redisKey;
    private int orgLen = -1, modLen = 0;
    private final Lock sync = new ReentrantLock();
    private List<String> cache = null;
    private Range cacheRange = null;
    private final List<Command> commands = new ArrayList();

    public RedisList(RedisKey redisKey) {
        this.redisKey = redisKey;
    }
    
    public List<String> getAll()
    {
        return this.in(0, this.length() - 1);
    }
    
    public String at(int index)
    {
        index = trueOffset(index);
        
        if (cache != null && cacheRange != null && cacheRange.contains(index))
        {
            return cache.get(cacheRange.transform(index));
        }
        
        Range atRange = new Range(index, index + 1);
        return this.range(atRange).get(0);
    }
    
    public List<String> in(int start, int end)
    {
        start = trueOffset(start);
        end = trueOffset(end + 1);
        if (cache != null && cacheRange != null && cacheRange.contains(start, end))
        {
            int tStart = cacheRange.transform(start);
            int tEnd = cacheRange.transform(end);
            return Collections.unmodifiableList(cache.subList(tStart, tEnd));
        }
        
        // update cache then call "in" angain
        this.cacheRange = new Range(start, end);
        this.cache = this.range(cacheRange);
        return this.in(start, end - 1);
    }
    
    public void append(String id)
    {
        try
        {
            sync.lock();
            ++modLen;
            this.commands.add(new AppendCommand(id, this.length()));
            this.cache = null;
            this.cacheRange = null;
        }
        finally
        {
            sync.unlock();
        }
    }
    
    public void push(String id)
    {
        try
        {
            sync.lock();
            ++modLen;
            this.commands.add(new PushCommand(id));
            this.cache = null;
            this.cacheRange = null;
        }
        finally
        {
            sync.unlock();
        }
    }
    
    /**
     * This method really harmful - like getAll method
     */
    public boolean contains(String id)
    {
        return getAll().contains(id);
    }
    
    public int length()
    {
        if (orgLen < 0)
        {
            orgLen = DBContext.Redis().llen(redisKey).intValue();
        }
        
        return orgLen + modLen;
    }

    @Override
    public void updateFromDB() {
        try
        {
            sync.lock();
            this.commands.clear();
            this.cache = null;
            this.cacheRange = null;
            modLen = 0;
        }
        finally
        {
            sync.unlock();
        }
    }

    @Override
    public void saveToDB() {
        try
        {
            sync.lock();
            // should use pipeline
            for (Command command : commands) {
                command.applyToDB(redisKey);
            }
            this.commands.clear();
            
            if (orgLen >= 0)
            {
                orgLen = this.length();
            }
            modLen = 0;
        }
        finally
        {
            sync.unlock();
        }
    }
    
    private int trueOffset(int off)
    {
        if (off < 0)
        {
            off = this.length() + off + 1;
        }
        
        return off;
    }
    
    private List<String> range(Range chRange)
    {
        try
        {
            sync.lock();
            Range orgRange = new Range(chRange.getMin(), chRange.getMax());
            List<Command> revCommands = Lists.reverse(commands);
            for (Command command : revCommands) {
                command.reverseRange(orgRange);
            }
            
            List<String> cacheList;
            if (orgRange.getLength() > 0)
            {
                cacheList = DBContext.Redis()
                    .lrange(redisKey, orgRange.getMin(), orgRange.getMax() - 1);
            }
            else
            {
                cacheList = new ArrayList(chRange.getLength());
            }
            
            for (Command command : commands) {
                command.applyToCache(cacheList, chRange);
            }
            
            return cacheList;
        }
        finally
        {
            sync.unlock();
        }   
    }
}