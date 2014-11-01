/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import config.CFPenguin;
import config.PGConfig;
import db.DBContext;
import java.util.HashMap;
import java.util.Map;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.services.EggStoreServices;
import share.PGError;
import share.PGHelper;
import db.PGKeys;
import share.PGMacro;
import db.RedisKey;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadPenguin")
public class Penguin implements PGEntity
{
    private final RedisKey redisKey;
    private final String uid;
    private final String coteID;
    private final String penguinID;
    
    private String kind;
    private int level;
    private int exp = 0;
    private int food;
    // last time ate
    private long lastEat;
    // last time spawn
    private long lastSpawn;
    private EggStoreServices.EggStorage lastEggStorage;

    public Penguin(String uid, String coteID, String penguinID) {
        this.uid = uid;
        this.coteID = coteID;
        this.penguinID = penguinID;
        
        this.redisKey = redisKey(uid, coteID, penguinID);
    }
    
    public static Penguin getPenguin(String uid, String coteID, String penguinID)
    {
        return EntityPool.inst().get(Penguin.class, uid, coteID, penguinID);
    }
    
    private static Penguin loadPenguin(String uid, String coteID, String penguinID)
    {
        Penguin penguin = new Penguin(uid, coteID, penguinID);
        PGException.Assert(penguin.isExist(),
                PGError.INVALID_PENGUIN,
                "Penguin " + penguinID +" are invalid");
        penguin.updateFromDB();
        
        return penguin;
    }
    
    private boolean isExist()
    {
        return DBContext.Redis().exists(this.redisKey());
    }
    
    public static Penguin newPenguin(String uid, String coteID, String penguinID,
            String kind, int level, long createdTime)
    {
        CFPenguin.Group.Level levelConfig = PGConfig.inst()
                .getPenguin().getGroup(kind).get(level);
        int exp = levelConfig.getExp();
        
        Penguin penguin = new Penguin(uid, coteID, penguinID);
        penguin.exp = exp;
        penguin.setLevel(level);
        penguin.setKind(kind);
        penguin.setFood(levelConfig.getFeed());
        penguin.setLastEat(createdTime);
        penguin.setLastSpawn(createdTime);
        penguin.setLastEggStorage(EggStoreServices.EggStorage.NONE);
        penguin.saveToDB();
        
        EntityPool.inst().put(penguin, Penguin.class, penguinID);
        
        return penguin;
    }
    
    public static void destroy(String uid, String coteID, String penguinID)
    {
        DBContext.Redis().del(redisKey(uid, coteID, penguinID));
        
        EntityPool.inst().remove(Penguin.class, penguinID);
    }
    
    private static RedisKey redisKey(String uid, String coteID, String penguinID)
    {
        return PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_COTES).getChild(coteID)
                .getChild(PGKeys.FD_PENGUINS).getChild(penguinID);
    }
    
    @Override
    public void updateFromDB()
    {
        Map<String, String> data = DBContext.Redis().hgetall(this.redisKey());
        
        this.level = (Integer.parseInt(data.get(PGMacro.LEVEL)));
        this.kind = (data.get(PGMacro.KIND));   
        this.exp = (Integer.parseInt(data.get(PGMacro.EXP)));
        this.food = (Integer.parseInt(data.get(PGMacro.FISH_LAST_EAT)));
        this.lastEat = (Long.parseLong(data.get(PGMacro.TIME_LAST_EAT)));
        this.lastSpawn = (Long.parseLong(data.get(PGMacro.TIME_LAST_SPAWN)));
        this.lastEggStorage = EggStoreServices.EggStorage
                .get(PGHelper.toInteger(data.get(PGMacro.EGG_STORE)));
    }

    @Override
    public void saveToDB()
    {        
        Map<String ,String> data = new HashMap();
        
        data.put(PGMacro.KIND, getKind());
        data.put(PGMacro.LEVEL, String.valueOf(getLevel()));
        data.put(PGMacro.EXP, String.valueOf(getExp()));
        data.put(PGMacro.FISH_LAST_EAT, String.valueOf(getFood()));
        data.put(PGMacro.TIME_LAST_EAT, String.valueOf(getLastEat()));
        data.put(PGMacro.TIME_LAST_SPAWN, String.valueOf(getLastSpawn()));
        data.put(PGMacro.EGG_STORE, String.valueOf(getLastEggStorage().getValue()));
        
        DBContext.Redis().hset(this.redisKey(), data);
    }
    
    private RedisKey redisKey()
    {
        return redisKey;
    }
    
    //<editor-fold defaultstate="collapsed" desc="AMF">
    public Map<String, Object> buildAMF()
    {
        Map<String, Object> data = new HashMap();
        
        data.put(PGMacro.PENGUIN_ID, getPenguinID());
        data.put(PGMacro.KIND, getKind());
        data.put(PGMacro.LEVEL, getLevel());
        data.put(PGMacro.EXP, getExp());
        data.put(PGMacro.FISH_LAST_EAT, getFood());
        data.put(PGMacro.TIME_LAST_EAT, getLastEat());
        data.put(PGMacro.TIME_LAST_SPAWN, getLastSpawn());
        
        return data;
    }
    //</editor-fold>

    public String getUid() {
        return uid;
    }

    public String getCoteID() {
        return coteID;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Properties">
    /**
     * @return the penguinID
     */
    public String getPenguinID() {
        return penguinID;
    }

    /**
     * @return the kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * @param kind the kind to set
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Never call this method - just increase penguin exp
     * @param level the level to set
     */
    @Deprecated
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the exp
     */
    public int getExp() {
        return exp;
    }

    /**
     * Don't call this method use: PGPenguinServices.increasePenguinExp instead
     * @param incExp
     */
    @Deprecated
    public void increaseExp(int incExp)
    {
        this.exp += incExp;
        
        if (this.exp > PGConfig.inst().getPenguin().getGroup(this.kind).maxExp())
        {
            this.exp = PGConfig.inst().getPenguin().getGroup(this.kind).maxExp();
        }
    }

    /**
     * @return the food
     */
    public int getFood() {
        return food;
    }

    /**
     * @param food the food to set
     */
    public void setFood(int food) {
        this.food = food;
    }

    /**
     * @return the lastEat
     */
    public long getLastEat() {
        return lastEat;
    }

    /**
     * @param lastEat the lastEat to set
     */
    public void setLastEat(long lastEat) {
        this.lastEat = lastEat;
    }

    /**
     * @return the lastSpawn
     */
    public long getLastSpawn() {
        return lastSpawn;
    }

    /**
     * @param lastSpawn the lastSpawn to set
     */
    public void setLastSpawn(long lastSpawn) {
        this.lastSpawn = lastSpawn;
    }

    public EggStoreServices.EggStorage getLastEggStorage() {
        return lastEggStorage;
    }

    public void setLastEggStorage(EggStoreServices.EggStorage lastEggStorage) {
        this.lastEggStorage = lastEggStorage;
    }

    //</editor-fold>
    
    @Override
    public String toString() {
        return "Penguin{" + "id=" + penguinID + ", kind=" + kind + ", level=" + level + ", exp=" + exp + ", food=" + food + ", lastEat=" + lastEat + ", lastSpawn=" + lastSpawn + '}';
    }
    
    public String shortDescription()
    {
        return "Penguin{" + "id=" + penguinID + ", kind=" + kind + ", level=" + level + '}';
    }
    
    public Object dump()
    {
        return DBContext.Redis().hgetall(this.redisKey());
    }
    
    public static void restore(String uid, String coteID, String pID,
            Map<String, String> data)
    {
        DBContext.Redis().hset(redisKey(uid, coteID, pID), data);
    }
}
