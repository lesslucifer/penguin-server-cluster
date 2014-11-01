/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.redis.list;

import java.util.List;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
interface Command {
    List<String> applyToCache(List<String> cache, Range cacheRange);
    void applyToDB(RedisKey key);
    void reverseRange(Range range);
}