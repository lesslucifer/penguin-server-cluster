/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.redis.PGRedisSetEntity;
import db.PGKeys;
import db.RedisKey;
import share.AMFBuilder;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadPenguinList")
public class PenguinList extends PGRedisSetEntity
{
    private final RedisKey keyPenguinList;
    private final String uid;
    private final String coteID;
    
    private PenguinList(String uid, String coteID)
    {
        this.uid = uid;
        this.coteID = coteID;
        keyPenguinList = redisKey(uid, coteID);
    }
    
    public static PenguinList getPenguinList(String uid, String coteID)
    {
        return EntityPool.inst().get(PenguinList.class, uid, coteID);
    }
    
    public static PenguinList loadPenguinList(String uid, String coteID)
    {
        return new PenguinList(uid, coteID);
    }
    
    public static void destroyList(String uid, String coteID)
    {
        DBContext.Redis().del(redisKey(uid, coteID));
        EntityPool.inst().remove(PenguinList.class, uid, coteID);
    }
    
    private static RedisKey redisKey(String uid, String coteID)
    {
        return PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_COTES)
                .getChild(coteID).getChild(PGKeys.FD_PENGUINS);
    }
    
    @Override
    protected RedisKey redisKey()
    {
        return keyPenguinList;
    }

    public String getUid() {
        return uid;
    }

    public String getCoteID() {
        return coteID;
    }
    
    public Map<String, Object> buildAMF()
    {
        return AMFBuilder.toAMF(this.getAll());
    }
    
    public Map<String, Object> buildFullAMF()
    {
        Set<Map<String, Object>> penguinAMFs = new HashSet();
        
        Set<String> penguinIDs = this.getAll();
        for (String penguinID : penguinIDs) {
            Penguin penguin = Penguin.getPenguin(uid, coteID, penguinID);
            penguinAMFs.add(penguin.buildAMF());
        }
        
        return AMFBuilder.toAMF(penguinAMFs);
    }
    
    public Object dump()
    {
        return this.getAll();
    }
}
