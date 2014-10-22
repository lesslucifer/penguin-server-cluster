/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.redis.PGRedisListEntity;
import db.PGKeys;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadNPCList")
public class NPCList extends PGRedisListEntity
{
    private final String uid;
    private final RedisKey redisKey;

    private NPCList(String uid) {
        this.uid = uid;
        
        redisKey = PGKeys.USERS.getChild(uid).getChild(PGKeys.FD_NPC_LIST);
    }
    
    public static NPCList getNPCList(String uid)
    {
        return EntityPool.inst().get(NPCList.class, uid);
    }
    
    private static NPCList loadNPCList(String uid)
    {
        return new NPCList(uid);
    }
    
    public static void destroyNPCList(String uid)
    {
        RedisKey redisKey =
                PGKeys.USERS.getChild(uid).getChild(PGKeys.FD_NPC_LIST);
        DBContext.Redis().del(redisKey);
        
        EntityPool.inst().remove(NPCList.class, uid);
    }

    public String getUid() {
        return uid;
    }
    
    @Override
    protected RedisKey redisKey() {
        return redisKey;
    }
    
    public Object dump()
    {
        return this.getAll();
    }
}
