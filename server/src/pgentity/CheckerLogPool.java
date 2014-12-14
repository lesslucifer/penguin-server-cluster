/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
public class CheckerLogPool {
    private final RedisKey key;

    private CheckerLogPool(RedisKey key) {
        this.key = key;
    }
    
    public static CheckerLogPool getPool(RedisKey key)
    {
        return new CheckerLogPool(key);
    }
    
    public RedisKey getKey(String field)
    {
        return key.getChild(field);
    }
}
