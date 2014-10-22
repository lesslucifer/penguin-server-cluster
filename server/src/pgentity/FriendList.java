/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.redis.PGRedisSetEntity;
import db.PGKeys;
import db.RedisKey;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadFriendList")
public class FriendList extends PGRedisSetEntity
{
    private final String uid;
    private final RedisKey keyFriendList;

    private FriendList(String uid)
    {
        this.uid = uid;
        keyFriendList = PGKeys.USERS.getChild(uid).getChild(PGKeys.FD_FRIENDSLIST);
    }
    
    public static FriendList getFriendList(String uid) throws PGException
    {
        return EntityPool.inst().get(FriendList.class, uid);
    }
    
    public static FriendList loadFriendList(String uid)
    {
        return new FriendList(uid);
    }
    
    public static void destroy(String uid)
    {
        DBContext.Redis()
                .del(PGKeys.USERS.getChild(uid).getChild(PGKeys.FD_FRIENDSLIST));
        EntityPool.inst().remove(FriendList.class, uid);
    }
    
    @Override
    protected RedisKey redisKey()
    {
        return keyFriendList;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public boolean contains(String ID) {
        return super.contains(ID);
    }
    
    //<editor-fold defaultstate="collapsed" desc="AMF">
    public Map<String, Object> buildAMF()
    {
        Map<String, Object> friendAMFs = new HashMap();
        
        Set<String> friendIDs = this.getAll();
        
        for (String friendID : friendIDs) {
            try
            {
                User friend = User.getUser(friendID);
                friendAMFs.put(friendID, friend.buildForFriendAMF());
            }
            catch (PGException pgEx)
            {
            }
        }
        
        return friendAMFs;
    }
    //</editor-fold>

    /**
     * User FriendServices instead
     * @deprecated
     */
    @Deprecated
    public Object dump()
    {
        return this.getAll();
    }
}
