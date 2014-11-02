/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.redis.list;

import db.DBContext;
import java.util.List;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
class PushCommand implements Command
{
    private final String value;

    public PushCommand(String value) {
        this.value = value;
    }
    
    @Override
    public List<String> applyToCache(List<String> cache, Range cacheRange) {
        if (cacheRange.getMin() == 0 && cacheRange.getMax() > 0)
        {
            cache.add(0, value);
        }
        
        return cache;
    }

    @Override
    public void applyToDB(RedisKey key) {
        DBContext.Redis().lpush(key, value);
    }

    @Override
    public void reverseRange(Range range) {
        if (range.getMin() > 0)
        {
            range.setMin(range.getMin() - 1);
        }
        
        if (range.getMax() > 0)
        {
            range.setMax(range.getMax() - 1);
        }
    }

    @Override
    public String toString() {
        return "PushCommand{" + "value=" + value + '}';
    }
}
