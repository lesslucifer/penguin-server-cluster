/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.redis.PGRedisSetEntity;
import db.PGKeys;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadList")
public class UserList extends PGRedisSetEntity
{
    public static enum ListType
    {
        ALL_USER(""),
        NPC("npcs"),
        WHITE_LIST("white"),
        BLACK_LIST("black"),
        SYSTEM_ACCOUNT("system");
        
        private final String field;

        private ListType(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }
    
    private final RedisKey redisKey;
    
    private UserList(ListType type)
    {
        this.redisKey = rkByType(type);
    }
    
    public static UserList getList(ListType type)
    {
        return EntityPool.inst().get(UserList.class, type);
    }
    
    private static UserList loadList(ListType type)
    {
        return new UserList(type);
    }
    
    private static RedisKey rkByType(ListType type)
    {
        if (type == ListType.ALL_USER)
        {
            return PGKeys.ALL_USERS;
        }
        
        return PGKeys.USER_LIST.getChild(type.getField());
    }
    
    @Override
    protected RedisKey redisKey() {
        return this.redisKey;
    }
}
