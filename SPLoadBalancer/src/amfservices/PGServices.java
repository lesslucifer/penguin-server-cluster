/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package amfservices;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import libCore.SNServices;
import libCore.config.Config;
import org.apache.log4j.*;
import share.Methods;
import share.PGConst;
import share.PGError;
import share.PGException;
import share.PGHelper;
import share.PGMacro;
import share.amf.AMFBuilder;
import target.RMITargetResolver;
import target.Request;
import target.Target;
import target.TargetResolver;
import zme.api.exception.ZingMeApiException;


/**
 *
 * @author linhta
 */
public class PGServices
{
    //========================= INITIALIZE =============================
    private static final Logger LOG = Logger.getLogger(PGServices.class.getName());

    private final amfservices.SessionCache activeUsers;
    private final TargetResolver targetResolver;
    
    public PGServices() throws PGException
    {
        super();
        
        this.activeUsers = SessionCache.inst();
        this.targetResolver = RMITargetResolver.inst();
    }
    
    //========================= AUTHENTICATION =============================
    
    public Map<String, Object> authenticate(Map<String, Object> params)
            throws IOException, ZingMeApiException
    {
        Map<String, Object> data = (Map) params.get("data");

        String uid = (String) data.get(PGMacro.UID);
        String sid = (String) data.get(PGMacro.SID);
        String signedReq = (String) data.get(PGMacro.SIGNED_REQUEST);

        SNServices zing = new SNServices(signedReq);
        PGException.Assert(uid.equals(zing.getUserInfo().getUid()),
                PGError.INVALID_USER, "Invalid signed request");

        activeUsers.putSession(sid, uid);
        
        return Collections.EMPTY_MAP;
    }
    
    public Map<String, Object> authenticateSystem(Map<String, Object> params) throws Exception
    {
        String sid = null;
        try
        {
            Map<String, Object> data = (Map) params.get("data");

            String uid = (String) data.get(PGMacro.UID);
            String adminPass = (String) data.get(PGMacro.SIGNED_REQUEST);

            boolean isSystemAccount = PGConst.ADMIN_TOOL_UID.equals(uid);
            if (!isSystemAccount)
            {
                Target masterTarget = this.targetResolver.getMasterTarget();
                
                long now = this.now();
                Request request = Request.makeAMF(null,
                        Methods.Global.IN_SYSTEM_LIST,
                        AMFBuilder.make(PGMacro.UID, uid),
                        now);
                isSystemAccount = (Boolean) masterTarget.doAMF(request);
            }
            
            PGException.Assert(isSystemAccount,
                    PGError.INVALID_USER, "User " + uid + " not be system account");
            PGException.Assert(PGConst.ADMIN_TOOL_PASSWORD.equals(adminPass),
                    PGError.INVALID_SIGNED_REQUEST, "User password are wrong!");

            sid = PGHelper.randomGUID();
            activeUsers.putSession(sid, uid);
            
            return AMFBuilder.make("data", AMFBuilder.make(PGMacro.SID, sid));
        }
        catch (PGException ex)
        {
            throw new Exception(ex.getMessage());
        }
    }
    
    //========================= SERVICES =============================
    
    public Map<String, Object> loadGame(Map<String, Object> params)
    {
        return reflectCall("loadGame", params);
    }
 
    //----------------------------------------------------------------------

    public Map<String, Object> getFriendList(Map<String, Object> params) 
    {
        return reflectCall("getFriendList", params);
    }
    
    //----------------------------------------------------------------------

    public Map<String, Object> visitFriend(Map<String, Object> params) 
    {
        // fake session id
        Map<String, Object> cheatParams = (Map<String, Object>) params.get("cheat");
        Map<String, Object> dataParams = (Map<String, Object>) params.get("data");
        
        String fid = (String) dataParams.get(PGMacro.FID);
        cheatParams.put(PGMacro.UID, fid);
        
        return reflectCall("visitFriend", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> buyPenguin(Map<String, Object> params) 
    {
        return reflectCall("buyPenguin", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> dropFish(Map<String, Object> params) 
    {
        return reflectCall("dropFish", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> buyFish(Map<String, Object> params) 
    {
        return reflectCall("buyFish", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> buyGold(Map<String, Object> params) 
    {
        return reflectCall("buyGold", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> sellPenguin(Map<String, Object> params) 
    {
        return reflectCall("sellPenguin", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> penguinWannaEat(Map<String, Object> params)
    {
        return this.reflectCall("penguinWannaEat", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> upgradeBoxEgg(Map<String, Object> params) 
    {
        return reflectCall("upgradeBoxEgg", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> upgradeCote(Map<String, Object> params) 
    {
        return reflectCall("upgradeCote", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> moveEggFromCoteToInventory(Map<String, Object> params) 
    {
        return reflectCall("moveEggFromCoteToInventory", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> moveEggFromBoxEggToInventory(Map<String, Object> params) 
    {
        return reflectCall("moveEggFromBoxEggToInventory", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> sellEggsFromInventory(Map<String, Object> params) 
    {
        return reflectCall("sellEggsFromInventory", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> stealEgg(Map<String, Object> params) 
    {
        return reflectCall("stealEgg", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> helpFriendFish(Map<String, Object> params) 
    {
        return reflectCall("helpFriendFish", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> spawnEgg(Map<String, Object> params) 
    {
        return reflectCall("spawnEgg", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> buyExpPenguin(Map<String, Object> params) 
    {
        return reflectCall("buyExpPenguin", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> buyLevelPenguin(Map<String, Object> params) 
    {
        return reflectCall("buyLevelPenguin", params);
    }
 
    //----------------------------------------------------------------------
    
    public Map<String, Object> renameCote(Map<String, Object> params) 
    {
        return reflectCall("renameCote", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> takeSnapshot(Map<String, Object> params) 
    {
        return reflectCall("takeSnapshot", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> takeAds(Map<String, Object> params) 
    {
        return reflectCall("takeAds", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> setUIState(Map<String, Object> params) 
    {
        return reflectCall("setUIState", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> saveSettings(Map<String, Object> params) 
    {
        return reflectCall("saveSettings", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> wakeDogUp(Map<String, Object> params) 
    {
        return reflectCall("wakeDogUp", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getMails(Map<String, Object> params) 
    {
        return reflectCall("getMails", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> clearAllMails(Map<String, Object> params) 
    {
        return reflectCall("clearAllMails", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getUserGifts(Map<String, Object> params) 
    {
        return reflectCall("getUserGifts", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> receiveGift(Map<String, Object> params) 
    {
        return reflectCall("receiveGift", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> destroyGift(Map<String, Object> params) 
    {
        return reflectCall("destroyGift", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> loginAward(Map<String, Object> params) 
    {
        return reflectCall("loginAward", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> geAchievements(Map<String, Object> params) 
    {
        return reflectCall("geAchievements", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> receiveAchievementPrize(Map<String, Object> params) 
    {
        return reflectCall("receiveAchievementPrize", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> takeRandomizePrize(Map<String, Object> params) 
    {
        return reflectCall("takeRandomizePrize", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> reloadFriendList(Map<String, Object> params) 
    {
        return reflectCall("reloadFriendList", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> wakeDogFirstTime(Map<String, Object> params) 
    {
        return reflectCall("wakeDogFirstTime", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> useGiftCode(Map<String, Object> params) 
    {
        return reflectCall("useGiftCode", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getPayment(Map<String, Object> params)
    {
        return reflectCall("getPayment", params);
    }
    
    //========================= FIEND SERVICES =============================
    
    public Map<String, Object> getFriendInfoDetail(Map<String, Object> params)
    {
        return reflectCall("getFriendInfoDetail", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> friendPenguinWannaEat(Map<String, Object> params)
    {
        return reflectCall("friendPenguinWannaEat", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> friendPenguinSpawnEgg(Map<String, Object> params)
    {
        return reflectCall("friendPenguinSpawnEgg", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getFriendAchievements(Map<String, Object> params)
    {
        return reflectCall("getFriendAchievements", params);
    }
    
    //========================= QUEST SERVICES =============================
    
    // <editor-fold defaultstate="collapsed" desc="QUEST SERVICES">
    public Map<String, Object> acceptDailyQuest(Map<String, Object> params) 
    {
        return reflectCall("acceptDailyQuest", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> returnDailyQuest(Map<String, Object> params) 
    {
        return reflectCall("returnDailyQuest", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> completeDailyQuestImmediately(Map<String, Object> params) 
    {
        return reflectCall("completeDailyQuestImmediately", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> returnMainQuest(Map<String, Object> params) 
    {
        return reflectCall("returnMainQuest", params);
    }
    // </editor-fold>
    
    //========================= RELOAD SERVICES ==========================

    // <editor-fold defaultstate="collapsed" desc="RELOAD SERVICES">
    
    public Map<String, Object> getUserInfoDetail(Map<String, Object> params) 
    {
        return reflectCall("getUserInfoDetail", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getPenguinInfo(Map<String, Object> params)
    {
        return reflectCall("getPenguinInfo", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getCoteInfo(Map<String, Object> params)
    {
        return reflectCall("getCoteInfo", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getCoteInfoDetail(Map<String, Object> params)
    {
        return reflectCall("getCoteInfoDetail", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getBoxEggInfo(Map<String, Object> params)
    {
        return reflectCall("getBoxEggInfo", params);
    }
    
    // </editor-fold>
    
    //========================= ADMIN TOOLS ===================================
    
    // <editor-fold defaultstate="collapsed" desc="ADMIN SERVICES">
    public Map<String, Object> increaseUserGold(Map<String, Object> params) 
    {
        return reflectCall("increaseUserGold", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> increaseUserCoin(Map<String, Object> params) 
    {
        return reflectCall("increaseUserCoin", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> increaseUserExp(Map<String, Object> params) 
    {
        return reflectCall("increaseUserExp", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> increaseUserTime(Map<String, Object> params) 
    {
        return reflectCall("increaseUserTime", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> sendGift(Map<String, Object> params) 
    {
        return reflectCall("sendGift", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> sendGiftToAllUsers(Map<String, Object> params) 
    {
        return reflectCall("sendGiftToAllUsers", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> makeGiftCodes(Map<String, Object> params) 
    {
        return reflectCall("makeGiftCodes", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> addWhiteList(Map<String, Object> params) 
    {
        return reflectCall("addWhiteList", params);
    }
    
    // </editor-fold>
    
    //----------------------------------------------------------------------
    
    //========================= REFLECT CALL =============================
    
    private Map<String, Object> reflectCall(String method, Map<String, Object> params) 
    {
        Map<String, Object> sessionParams = (Map) params.get("session");
        final String uid = this.validSession(sessionParams);
        
        Map<String, Object> content = new HashMap<String, Object>(5);
        
        try
        {
            // put time
            long now = this.now();
            now = convertForCheat(now, params);
            content.put("timeServer", now);

            // compare client config
            this.validConfig(content, (Map<String, Object>) params.get("config"));
            
            Map<String, Object> paramData = Collections.EMPTY_MAP;
            if (params.get("data") instanceof Map)
            {
                paramData = (Map) params.get("data");
            }
            
            Request request = Request.makeAMF(uid, method, paramData, now);
            Target target = this.targetResolver.getUserTarget(uid);
            Object resp = target.doAMF(request);

            this.putError(null, 0, content);
            content.put("data", resp);
        }
        catch (InvocationTargetException ex)
        {
            this.putError(ex.getCause(), content);
            if (ex.getCause() instanceof PGException)
            {
                LOG.error("Service " + method + " error: " + ex.getCause().getMessage());
            }
            else if (ex.getCause() instanceof java.lang.ExceptionInInitializerError)
            {
                LOG.error("Service " + method, ex.getCause().getCause());
            }
            else
            {
                LOG.error("Service " + method, ex.getCause());
            }
        } catch (SecurityException ex) {
            this.putError(ex, content);
        } catch (IllegalArgumentException ex) {
            this.putError(ex, content);
        }
        
        return content;
    }
    
    //========================= ERROR REPORTING =============================
    
    private void putError(Throwable e, Map<String, Object> response)
    {
        int error_code = PGError.UNDEFINED;
        if (e instanceof PGException)
        {
            PGException pgException = (PGException) e;
            error_code = pgException.getErrorCode();
        }
        this.putError(e, error_code, response);
    }
    
    private void putError(Throwable e, int error_code, Map<String, Object> response)
    {
        response.put("error", error_code);
        if (e != null)
        {
            response.put("error_message", e.toString());
        }
        else
        {
            response.put("error_message", "");
        }
    }
    
    private long now()
    {
        return System.currentTimeMillis();
    }
    
    private void validConfig(Map<String, Object> content, Map<String, Object> clientConfigParams)
            throws InvocationTargetException
    {
        String strClientConfigVersion = (String) clientConfigParams.get("version");
        VersionString clientConfigVersion = new VersionString(strClientConfigVersion == null?"0.0":strClientConfigVersion);
        VersionString serverConfigVersion = new VersionString(Config.getParam("config", "version"));
        if (clientConfigVersion.compareTo(serverConfigVersion) != 0)
        {
            Map<String, Object> config = new HashMap<String, Object>(2);
            config.put("version", serverConfigVersion.toString());
            Target master = targetResolver.getMasterTarget();
            Request request = Request.makeAMF(null, Methods.Global.GET_ALL_CONFIGS, null, 0);
            config.put("config", master.doAMF(request));

            content.put("config", config);
        }
    }

    private String validSession(Map<String, Object> session)
    {
        String sid = (String) session.get(PGMacro.SID);
        PGException.Assert(sid != null, PGError.INVALID_SESSION, "Empty session");
        
        String uid = (String) session.get(PGMacro.UID);
        String actUid = this.activeUsers.getUID(sid);
        
        PGException.Assert((uid != null) && (uid.equals(actUid)),
                PGError.INVALID_SESSION, "Invalid session");
        
        return actUid;
    }
    
    private long convertForCheat(long realNow, Map<String, Object> data)
            throws InvocationTargetException
    {
        if (data.containsKey("cheat"))
        {
            Map<String, Object> cheatData = (Map<String, Object>) data.get("cheat");
            String uid = (String) cheatData.get(PGMacro.UID);
            
            Target masterTarget = targetResolver.getMasterTarget();
            Request request = Request.makeAMF(null,
                    Methods.Global.GET_HACK_TIME,
                    AMFBuilder.make(PGMacro.UID, uid), realNow);
            long hackTime = (Long) masterTarget.doAMF(request);
            
            return realNow + hackTime;
        }
        
        return realNow;
    }
}