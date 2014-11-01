/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.HashMap;
import java.util.Map;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.services.EggStoreServices;
import db.PGKeys;
import share.PGMacro;
import db.RedisKey;

/**
 * BoxEgg
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadBoxEgg")
public class BoxEgg implements PGEntity
{
    private final RedisKey redisKey;
    private final EggStore eggs;
    
    private int level;
    
    private BoxEgg(String uid, String coteID)
    {
        this.redisKey = redisKey(uid, coteID);
        RedisKey eggStoreKey = redisKey.getChild(uid);
        eggs = EggStore.getEggStore(eggStoreKey);
    }
    
    public static BoxEgg getBoxEgg(String uid, String coteID)
    {
        return EntityPool.inst().get(BoxEgg.class, uid, coteID);
    }
    
    public static BoxEgg loadBoxEgg(String uid, String coteID)
    {
        BoxEgg boxEgg = new BoxEgg(uid, coteID);
        boxEgg.updateFromDB();
        
        return boxEgg;
    }
    
    public static BoxEgg newBoxEgg(String uid, String coteID, int lvl)
    {
        BoxEgg boxEgg = new BoxEgg(uid, coteID);
        boxEgg.setLevel(lvl);
        boxEgg.saveToDB();
        
        EntityPool.inst().put(boxEgg, BoxEgg.class, uid, coteID);
        
        return boxEgg;
    }
    
    public static void destroy(String uid, String coteID)
    {
        RedisKey redisKey = redisKey(uid, coteID);
        DBContext.Redis().del(redisKey);
        EggStore.destroyEggStore(redisKey.getChild(PGKeys.FD_EGGS));
        
        EntityPool.inst().remove(BoxEgg.class, uid, coteID);
    }
    
    private static RedisKey redisKey(String uid, String coteID)
    {
        return PGKeys.USERS.getChild(uid).getChild(PGKeys.FD_COTES)
                .getChild(coteID).getChild(PGKeys.FD_BOXEGG);
    }
    
    @Override
    public void updateFromDB()
    {
        Map<String, String> data = DBContext.Redis().hgetall(this.redisKey());
        
        this.level = (Integer.parseInt(data.get(PGMacro.LEVEL)));
    }

    @Override
    public void saveToDB()
    {
        Map<String, String> data = new HashMap();
        
        data.put(PGMacro.LEVEL, String.valueOf(getLevel()));
        DBContext.Redis().hset(this.redisKey(), data);
    }
    
    private RedisKey redisKey()
    {
        return this.redisKey;
    }
    
    //<editor-fold defaultstate="collapsed" desc="AMFs">
    public Map<String, Object> buildAMF()
    {
        Map<String,Object> data = new HashMap<String, Object>();
        
        data.put(PGMacro.LEVEL, getLevel());
        data.put(PGMacro.EGGS, EggStoreServices.inst().buildAMF(this.eggStore()));
        
        return data;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    
    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }
    
    public EggStore eggStore()
    {
        return this.eggs;
    }
    
    public Object dump()
    {
        Map<String, Object> data = (Map) DBContext.Redis().hgetall(redisKey());
        data.put(PGMacro.EGGS, this.eggs.dump());
        
        return data;
    }
    
    public static void restore(String uid, String coteID, Map<String, String> data)
    {
        DBContext.Redis().hset(redisKey(uid, coteID), data);
    }
}
