/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.redis.list;

import db.DBContext;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import pgentity.PGEntity;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
class FragmentList implements PGEntity
{
    private final RedisKey redisKey;
    private int orgLen = -1, modLen = 0;
    private Lock sync = new ReentrantLock();
    private List<Command> commands = new LinkedList();
    private NavigableMap<Integer, List<String>> fragCache = new TreeMap();

    public FragmentList(RedisKey redisKey) {
        this.redisKey = redisKey;
    }
    
    public List<String> getAll()
    {
        return this.in(0, this.length() - 1);
    }
    
    public String at(int index)
    {
        index = trueOffset(index);
        
        if (cacheContainsIndex(index))
        {
            Integer floorIdx = fragCache.floorKey(index);
            List<String> frag = fragCache.get(floorIdx);
            return frag.get(index - floorIdx);
        }
        
        return this.in(index, index).get(0);
    }
    
    public List<String> in(int start, int end)
    {
        start = trueOffset(start);
        end = trueOffset(end + 1);
        
        if (cacheContainsRange(start, end))
        {
            Integer floorIdx = fragCache.floorKey(start);
            List<String> frag = fragCache.get(floorIdx);
            return Collections.unmodifiableList(
                    frag.subList(start - floorIdx, end - floorIdx));
        }
        
        
        return this.in(start, end - 1);
    }
    
    public void append(String id)
    {
        try
        {
            sync.lock();
            ++modLen;
            this.commands.add(new AppendCommand(id, this.length()));
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
            orgLen = (int) DBContext.Redis().llen(redisKey);
        }
        
        return orgLen + modLen;
    }

    @Override
    public void updateFromDB() {
        try
        {
            sync.lock();
            this.commands.clear();
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
    
    private boolean cacheContainsIndex(int index)
    {
        Integer floorIndex = this.fragCache.floorKey(index);
        if (floorIndex != null)
        {
            List<String> frag = this.fragCache.get(floorIndex);
            return floorIndex + frag.size() > index;
        }
        
        return false;
    }
    
    private boolean cacheContainsRange(int start, int end)
    {
        Integer floorIndex = this.fragCache.floorKey(start);
        if (floorIndex != null)
        {
            List<String> frag = this.fragCache.get(floorIndex);
            return floorIndex + frag.size() >= end;
        }
        
        return false;
    }
}
