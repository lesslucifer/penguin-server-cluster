/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.HashMap;
import java.util.Map;
import share.PGLog;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.services.EggStoreServices;
import share.PGError;
import db.PGKeys;
import share.PGMacro;
import db.RedisKey;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadCote")
public class Cote implements PGEntity
{
    private final RedisKey redisKey;
    
    private final String uid;
    private final String coteID;
    private final PenguinList penguinsEntity;
    private final EggStore eggsEntity;
    
    private int level;
    private int poolFish;
    private String coteName;

    private Cote(String uid, String coteID)
    {
        this.redisKey = redisKey(uid, coteID);
        this.uid = uid;
        this.coteID = coteID;
        this.penguinsEntity = PenguinList.getPenguinList(uid, coteID);
        this.eggsEntity = EggStore.getEggStore(redisKey.getChild(PGKeys.FD_EGGS));
    }
    
    /**
     * Static factory
     * @param coteID
     * @return Cote entity
     * @throws PGException if coteID aren't exist in db
     */
    public static Cote getCote(String uid, String coteID) throws PGException
    {
        return EntityPool.inst().get(Cote.class, uid, coteID);
    }
    
    /**
     * Static factory
     * @param uid
     * @param coteID
     * @return Cote entity
     * @throws PGException if coteID aren't exist in db
     */
    public static Cote loadCote(String uid, String coteID) throws PGException
    {
        Cote cote = new Cote(uid, coteID);
        PGException.Assert(cote.isExist(),
                PGError.INVALID_COTE, "Invalid cote: " + coteID);
        
        cote.updateFromDB();
        
        return cote;
    }
    
    /**
     * Create a new (blank) cote entity
     * without any database check/update/save
     * NERVER USE THIS METHOD - USED BY <i>PGCoteServices.createCote</i> instead
     * @param uid
     * @param coteID
     * @return Blank entity - contains no data
     */
    @Deprecated
    public static Cote newCote(String uid, String coteID, int level,
            int poolFish, String name)
    {
        Cote cote = new Cote(uid, coteID);
        cote.level = level;
        cote.poolFish = poolFish;
        cote.coteName = name;
        cote.saveToDB();
        
        EntityPool.inst().put(cote, Cote.class, coteID);
        
        return cote;
    }
    
    /**
     * use CoteServicce.destroyCote instead
     * @param coteID
     */
    @Deprecated
    public static void destroy(String uid, String coteID)
    {
        RedisKey redisKey = redisKey(uid, coteID);
        DBContext.Redis().del(redisKey);
        RedisKey eggStoreKey = redisKey.getChild(PGKeys.FD_EGGS);
        EggStore.destroyEggStore(eggStoreKey);
        
        EntityPool.inst().remove(Cote.class, coteID);
    }
    
    private static RedisKey redisKey(String uid, String coteID)
    {
        return PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_COTES).getChild(coteID);
    }
    
    @Override
    public void updateFromDB()
    {
        Map<String, String> data = DBContext.Redis().hgetall(this.redisKey());
        
        this.level = (Integer.parseInt(data.get(PGMacro.LEVEL)));
        this.poolFish = (Integer.parseInt(data.get(PGMacro.FISH)));
        this.coteName = data.get(PGMacro.NAME);
    }

    @Override
    public void saveToDB()
    {
        Map<String, String> data = new HashMap();
        
        data.put(PGMacro.LEVEL, String.valueOf(level));
        data.put(PGMacro.FISH, String.valueOf(poolFish));
        data.put(PGMacro.NAME, coteName);
        
        DBContext.Redis().hset(this.redisKey(), data);
    }
    
    private RedisKey redisKey()
    {
        return redisKey;
    }
    
    public boolean isExist()
    {
        return DBContext.Redis().exists(this.redisKey());
    }
    
    //<editor-fold defaultstate="collapsed" desc="AMF">
    public Map<String, Object> buildBasicAMF()
    {
        Map<String, Object> data = new HashMap<String, Object>();

        data.put(PGMacro.COTE_ID, getCoteID());
        data.put(PGMacro.LEVEL, getLevel());
        data.put(PGMacro.FISH, getPoolFish());
        data.put(PGMacro.NAME, getCoteName());

        return data;
    }
    
    public Map<String, Object> buildRescusiveAMF(boolean buildBoxEgg, boolean buildPenguins,
            boolean buildEggs, boolean buildDog)
    {
        Map<String, Object> data = this.buildBasicAMF();

        if (buildPenguins)
        {
            data.put(PGMacro.PENGUIN_LIST, this.penguinsEntity.buildFullAMF());
        }
        if (buildBoxEgg)
        {
            BoxEgg boxEgg = BoxEgg.getBoxEgg(uid, coteID);
            data.put(PGMacro.BOXEGG, boxEgg.buildAMF());
        }
        if (buildEggs)
        {
            data.put(PGMacro.EGGS,
                    EggStoreServices.inst().buildLimitedAMF(this.eggsEntity));
        }
        if (buildDog)
        {
            Dog dog = Dog.getDog(uid, coteID);
            data.put(PGMacro.DOG, dog.buildAMF());
        }

        return data;
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Properties">

    public String getUid() {
        return uid;
    }
    
    /**
     * @return the coteID
     */
    public String getCoteID() {
        return coteID;
    }

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

    /**
     * @return the poolFish
     */
    public int getPoolFish() {
        return poolFish;
    }

    /**
     * @param poolFish the poolFish to set
     */
    public void setPoolFish(int poolFish) {
        this.poolFish = poolFish;
        PGLog.debug("Pool fish changed: %d", poolFish);
    }

    public String getCoteName() {
        return coteName;
    }

    public void setCoteName(String coteName) {
        this.coteName = coteName;
    }
    //</editor-fold>

    /**
     * @return the penguinsEntity
     */
    public PenguinList penguins() {
        return penguinsEntity;
    }

    /**
     * @return the eggsEntity
     */
    public EggStore eggStore() {
        return eggsEntity;
    }
    
    public Object dump()
    {
        return DBContext.Redis().hgetall(redisKey());
    }
    
    public static void restore(String uid, String coteID, Map<String, String> data)
    {
        DBContext.Redis().hset(redisKey(uid, coteID), data);
    }
}
