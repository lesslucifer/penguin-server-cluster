/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import config.CFUser;
import config.PGConfig;
import db.DBContext;
import db.PGKeys;
import db.RedisKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import libCore.config.Config;
import share.PGLog;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.pool.PooledEntity;
import share.AMFBuilder;
import share.PGError;
import share.PGException;
import share.PGHelper;
import share.PGMacro;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadUser")
public class User implements PGEntity, PooledEntity
{
    private final RedisKey redisKey;
    private final String uid;
    private final FriendList friendListEntity;
    private final CoteList coteListEntity;
    private final Inventory inventoryEntity;
    private final UIData tutEntity;
    private final UserSettings settingsEntity;
    
    private int fish;
    
    private int level;
    private int exp;
    
    private int gold;
    private int coin;
    
    private String name;
    private String avatar;
    
    private String lastCote;
    
    private int dbVer;
    
    private User(String uid)
    {
        this.redisKey = redisKey(uid);
        this.uid = uid;
        this.friendListEntity = FriendList.getFriendList(uid);
        this.coteListEntity = new CoteList(uid);
        this.inventoryEntity = Inventory.getInventory(uid);
        this.tutEntity = UIData.getEntity(uid);
        this.settingsEntity = UserSettings.getEntity(uid);
    }
    
    public static User getUser(String uid)
            throws PGException
    {
        return EntityPool.inst().get(User.class, uid);
    }
    
    private static User loadUser(String uid)
    {
        PGException.Assert(isExist(uid),
                PGError.INVALID_USER, "User " + uid + " doesn't exist");
        
        User user = new User(uid);
        user.updateFromDB();
        
        return user;
    }
    
    /**
     * Use PGUseServices.createUser for create new user
     * @param uid
     * @param name
     * @param ava
     * @param now
     * @return 
     * @deprecated
     */
    @Deprecated
    public static User newUser(String uid, String name,
            String ava, long now)
    {
        User user = new User(uid);
        
        CFUser.Default defaultUser = PGConfig.inst().getUser().getDefaultUser();
        user.level = defaultUser.getLevel();
        user.exp = PGConfig.inst().getUser().get(user.level).getExp();
        user.fish = defaultUser.getFish();
        user.gold = defaultUser.getGold();
        user.coin = defaultUser.getCoin();
        
        user.avatar = ava;
        user.name = name;
        
        user.dbVer = PGHelper.toInteger(Config.getParam("db_version", "db_ver"));
        user.saveToDB();
        
        EntityPool.inst().put(user, User.class, uid);
        
        return user;
    }
    
    /**
     * Use UserServices.destroyUser instead
     * @param uid
     * @deprecated
     */
    @Deprecated
    public static void destroy(String uid)
    {
        DBContext.Redis().del(PGKeys.ALL_USERS.getChild(uid));
        EntityPool.inst().remove(User.class, uid);
    }
    
    public static boolean isExist(String uid)
    {
        return DBContext.Redis().exists(PGKeys.USERS.getChild(uid)) &&
                DBContext.Redis().sismember(PGKeys.ALL_USERS, uid);
    }
    
    public static RedisKey redisKey(String uid)
    {
        return PGKeys.USERS.getChild(uid);
    }
    
    @Override
    public void updateFromDB()
    {
        Map<String, String> data = DBContext.Redis().hgetall(this.redisKey());
        
        this.level = (Integer.parseInt(data.get(PGMacro.LEVEL)));
        this.fish = (Integer.parseInt(data.get(PGMacro.FISH)));
        this.gold = (Integer.parseInt(data.get(PGMacro.GOLD)));
        this.coin = (Integer.parseInt(data.get(PGMacro.COIN)));
        this.exp = (Integer.parseInt(data.get(PGMacro.EXP)));
        this.avatar = (data.get(PGMacro.AVATAR));
        this.name = (data.get(PGMacro.NAME));
        this.lastCote = data.get(PGMacro.LAST_COTE);
        this.dbVer = PGHelper.toInteger(data.get(PGMacro.DB_VERSION));
    }

    @Override
    public void saveToDB()
    { 
        Map<String, String> data = new HashMap<String, String>();

        data.put(PGMacro.FISH, String.valueOf(getFish()));
        data.put(PGMacro.GOLD, String.valueOf(getGold()));
        data.put(PGMacro.COIN, String.valueOf(getCoin()));
        data.put(PGMacro.EXP, String.valueOf(getExp()));
        data.put(PGMacro.LEVEL, String.valueOf(getLevel()));
        data.put(PGMacro.AVATAR, getAvatar());
        data.put(PGMacro.NAME, getName());
        data.put(PGMacro.LAST_COTE, getLastCote());
        data.put(PGMacro.DB_VERSION, String.valueOf(getDbVer()));
        
        DBContext.Redis().hset(this.redisKey(), data);
    }
    
    protected RedisKey redisKey()
    {
        return this.redisKey;
    }
    
    //<editor-fold defaultstate="collapsed" desc="AMFs">
    public Map<String, Object> buildForFriendAMF()
    {
        Map<String, Object> amf = new HashMap();
        
        amf.put(PGMacro.EXP,  getExp());
        amf.put(PGMacro.LEVEL,  getLevel());
        amf.put(PGMacro.AVATAR, getAvatar());
        amf.put(PGMacro.NAME, getName());
        
        return amf;
    }
    
    public Map<String, Object> buildBasicDataAMF()
    {
        Map<String, Object> amf = buildForFriendAMF();
        
        amf.put(PGMacro.FISH, getFish());
        amf.put(PGMacro.GOLD, getGold());
        amf.put(PGMacro.COIN, getCoin());
        amf.put(PGMacro.LAST_COTE, getLastCote());
        
        return amf;
    }
    
    public Map<String, Object> buildFullAMF(boolean buildCote, boolean buildFriends,
            boolean buildInventory, boolean buildOthers, long now)
    {
        Map<String, Object> amf = this.buildBasicDataAMF();
        
        if (buildCote)
        {
            Collection<String> coteIDs = coteListEntity.getAll();
            String lastVisitedCote = getLastCote();
            List<Object> cotes = new ArrayList(coteIDs.size());
            for (String coteID : coteIDs) {
                Object coteData;
                Cote cote = Cote.getCote(uid, coteID);
                if (coteID.equals(lastVisitedCote))
                {
                    coteData = cote.buildRescusiveAMF(true, true, true, true);
                }
                else
                {
                    coteData = cote.buildBasicAMF();
                }
                
                cotes.add(cotes.size(), coteData);
            }
            amf.put(PGMacro.COTE, AMFBuilder.toAMF(cotes));
        }
        
        if (buildFriends)
        {
            amf.put(PGMacro.FRIEND_LIST, this.friendListEntity.buildAMF());
        }
        
        if (buildInventory)
        {
            amf.put(PGMacro.INVENTORY, this.inventory().buildAMF());
        }
        
        if (buildOthers)
        {
            UserGifts gifts = UserGifts.getGift(uid);
            amf.put(PGMacro.GIFTS, gifts.buildAMF());
            
            amf.put(PGMacro.UIDATA, this.uiData().buildAMF());
            amf.put(PGMacro.SETTINGS, this.settings().buildAMF());
            Penguindex penguindex = Penguindex.getPenguindex(uid);
            amf.put(PGMacro.PENGUINDEX, AMFBuilder.toAMF(penguindex.getAll()));
            UserTempData uTempData = UserTempData.getTempData(uid);
            amf.put(PGMacro.USER_TEMP_DATA, uTempData.buildAMF());
            UserDailyData uDailyData = UserDailyData.getData(uid, now);
            amf.put(PGMacro.USER_DAILY_DATA, uDailyData.buildAMF());
            MailBox mailBox = MailBox.getMailBoxOf(uid);
            amf.put(PGMacro.MAIL, AMFBuilder
                    .make(PGMacro.NUMBER_NEW_MAILS, mailBox.getUnreadMail()));
            amf.put(PGMacro.NOTIFICATIONS,
                    Notification.getNotif(uid).takeAllNotifications());
        }
        
        return amf;
    }
    //</editor-fold>
    
    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @return the fish
     */
    public int getFish() {
        return fish;
    }

    /**
     * @param fish the fish to set
     */
    public void setFish(int fish) {
        this.fish = fish;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Use increase user exp instead of
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
     * Use increase user exp in PGUserService instead of
     * @param incExp
     */
    @Deprecated
    public void increaseExp(int incExp)
    {
        this.exp += incExp;
        
        if (this.exp > PGConfig.inst().getUser().maxExp())
        {
            this.exp = PGConfig.inst().getUser().maxExp();
        }
        PGLog.debug("User exp changed: %d", exp);
    }

    /**
     * @return the gold
     */
    public int getGold() {
        return gold;
    }
    
    /**
     * Don't use this method - Use PGUserService instead
     * @param gold
     * @deprecated
     */
    @Deprecated
    public void increaseGold(int gold)
    {
        this.gold += gold;
    }
    
    /**
     * Don't use this method - Use PGUserService instead
     * @param gold
     * @deprecated
     */
    @Deprecated
    public void decreaseGold(int gold)
    {
        this.gold -= gold;
        if (this.gold < 0)
        {
            this.gold = 0;
        }
    }
    
    @Deprecated
    public void changeCoin(int dCoin)
    {
        if (dCoin != 0)
        {
            this.coin += dCoin;
            this.coin = (this.coin < 0)?0:this.coin;
        }
    }

    /**
     * @return the coin
     */
    public int getCoin() {
        return coin;
    }

    /**
     * @return the NAME
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the NAME to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * @param avatar the avatar to set
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public FriendList friendList()
    {
        return this.friendListEntity;
    }
    
    public CoteList cotes()
    {
        return this.coteListEntity;
    }
    
    public Inventory inventory()
    {
        return this.inventoryEntity;
    }

    public UIData uiData() {
        return tutEntity;
    }
    
    public UserSettings settings()
    {
        return settingsEntity;
    }

    public int getDbVer() {
        return dbVer;
    }

    public void setDbVer(int dbVer) {
        this.dbVer = dbVer;
    }

    public String getLastCote() {
        lastCote = (!PGHelper.isNullOrEmpty(lastCote))?lastCote:coteListEntity.at(0);
        lastCote = (!PGHelper.isNullOrEmpty(lastCote))?lastCote:"";
        
        return lastCote;
    }

    public void setLastCote(String lastCote) {
        this.lastCote = lastCote;
    }
    
    /**
     * use UserServices.dumpUser instead
     * @return
     * @deprecated
     */
    @Deprecated
    public Object dump()
    {
        return DBContext.Redis().hgetall(redisKey());
    }
}
