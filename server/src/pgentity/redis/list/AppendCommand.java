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
class AppendCommand implements Command {
    private final String value;
    private final int capturedLength;

    public AppendCommand(String value, int len) {
        this.value = value;
        this.capturedLength = len;
    }

    @Override
    public List<String> applyToCache(List<String> cache, Range cacheRange) {
        if (cacheRange.contains(capturedLength, capturedLength))
        {
            cache.add(value);
        }
        
        return cache;
    }

    @Override
    public void applyToDB(RedisKey key) {
        DBContext.Redis().lappend(key, value);
    }

    @Override
    public void reverseRange(Range range) {
        if (range.getMin() >= capturedLength)
        {
            range.setMin(range.getMin() - 1);
        }
        
        if (range.getMax() >= capturedLength)
        {
            range.setMax(range.getMax() - 1);
        }
    }

    @Override
    public String toString() {
        return "AppendCommand{" + "value=" + value + ", capturedLength=" + capturedLength + '}';
    }
}