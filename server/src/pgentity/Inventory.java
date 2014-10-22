/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import java.util.HashMap;
import java.util.Map;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.services.EggStoreServices;
import db.PGKeys;
import share.PGMacro;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadInventory")
public class Inventory implements PGEntity
{
    private final String uid;
    private final RedisKey redisKey;
    private final EggStore eggs;
    
    private Inventory(String uid)
    {
        this.uid = uid;
        this.redisKey = redisKey(uid);
        this.eggs = EggStore.getEggStore(redisKey.getChild(PGKeys.FD_EGGS));
    }
    public static Inventory getInventory(String uid)
    {
        return EntityPool.inst().get(Inventory.class, uid);
    }
    
    private static Inventory loadInventory(String uid)
    {
        Inventory inventory = new Inventory(uid);
        inventory.updateFromDB();
        
        return inventory;
    }
    
    public static void destroy(String uid)
    {
        RedisKey eggStoreKey = redisKey(uid).getChild(PGKeys.FD_EGGS);
        EggStore.destroyEggStore(eggStoreKey);
        
        EntityPool.inst().remove(Inventory.class, uid);
    }
    
    public static RedisKey redisKey(String uid)
    {
        return User.redisKey(uid).getChild(PGKeys.FD_INVENTORY);
    }
    
    @Override
    public void updateFromDB()
    {
    }

    @Override
    public void saveToDB()
    {
    }
    
    public Map<String, Object> buildAMF()
    {
        Map<String, Object> data = new HashMap();
        data.put(PGMacro.EGGS,
                EggStoreServices.inst().buildAMF(this.eggStore()));
        
        return data;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    public String getUID()
    {
        return this.uid;
    }
    
    public EggStore eggStore()
    {
        return this.eggs;
    }
    
    public int numberItems()
    {
        return this.eggs.nOfEggs();
    }
    //</editor-fold>

    public Object dump()
    {
        Map<String, Object> data = new HashMap(1);
        data.put(PGMacro.EGGS, this.eggs.dump());
        
        return data;
    }
}
