/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.reflect;

import git.target.Targets;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import libCore.config.Config;
import org.apache.log4j.Logger;
import share.Methods;
import share.PGError;
import share.PGException;
import share.PGHelper;
import share.PGLog;
import share.PGMacro;
import target.Request;
import target.Response;
import target.Target;
import target.TargetResolver;

/**
 *
 * @author KieuAnh
 */
public class Reflector implements amfservices.Reflector {
    
    private static final Logger LOG = Logger.getLogger(Reflector.class.getName());
    private final SessionCache activeUsers;
    private final TargetResolver targetResolver;

    public Reflector() {
        this.activeUsers = SessionCache.inst();
        this.targetResolver = Targets.RESOLVER;
    }
    
    //========================= AUTHENTICATION =============================
    
    @Override
    public Map<String, Object> authenticate(Map<String, Object> params)
    {
//        return Authenticate.authenticate(activeUsers, params);
        return Collections.EMPTY_MAP;
    }
    
    @Override
    public Map<String, Object> authenticateSystem(Map<String, Object> params)
    {
        return Collections.EMPTY_MAP;
//        return Authenticate.authenticateSystem(activeUsers, targetResolver,
//                params, this.now());
    }
    
    @Override
    public Map<String, Object> reflectCall(String method, Map<String, Object> params) 
    {
        long start = System.currentTimeMillis();
        //Logging
        Map<String, Object> sessionParams = (Map) params.get("session");
        String uid = this.validSession(sessionParams);
        PGLog.info("%s call service: %s", uid, method);
        
        Map<String, Object> content = new HashMap<>();
        
        try
        {
            // put time
            long now = this.now();
            now = convertForCheat(now, params);
            content.put("timeServer", now);
            PGLog.debug("Time: %s", now);

            // compare client config
            this.validConfig(content, (Map<String, Object>) params.get("config"));
            
            Map<String, Object> data = Collections.EMPTY_MAP;
            if (params.get("data") instanceof Map)
            {
                data = (Map) params.get("data");
            }

            PGLog.debug("Input\r\n%s", PGHelper.obj2PrettyJSON(data));
            Target target = this.targetResolver.getUserTarget(uid);
            Request req = Request.makeAMF(uid, method, data, now);
            Response respData = (Response) target.doAMF(req);
            content.putAll(respData.toAMF());
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
        }
        catch (Exception ex) {
            this.putError(ex, content);
        }
        finally
        {
            long end = System.currentTimeMillis();
            PGLog.info(uid + " end services " + method +
                    " (" + ((double) (end - start)) / 1000.0 + ")");
            
            if (((Number)content.get("error")).intValue() < 0)
            {
                PGLog.info("Service " + method + "error: " + content.get("error_message"));
            }
        }
        
        PGLog.debug("Output\r\n%s", PGHelper.obj2PrettyJSON((content)));
        
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
    {
        String strClientConfigVersion = (String) clientConfigParams.get("version");
        VersionString clientConfigVersion = new VersionString(strClientConfigVersion == null?"0.0":strClientConfigVersion);
        VersionString serverConfigVersion = new VersionString(Config.getParam("config", "version"));
        if (clientConfigVersion.compareTo(serverConfigVersion) != 0)
        {
            try {
                Map<String, Object> config = new HashMap<>();
                config.put("version", serverConfigVersion.toString());
                Target master = targetResolver.getMasterTarget();
                Request req = Request.makeAMF(null, Methods.Global.GET_ALL_CONFIGS, null, 0);
                Response confResp = (Response) master.doAMF(req);
                config.put("config", confResp.getData());
                
                content.put("config", config);
            } catch (InvocationTargetException ex) {
                content.put("config", Collections.EMPTY_MAP);
            }
        }
    }

    private String validSession(Map<String, Object> session)
    {
        String uid = (String) session.get(PGMacro.UID);
        return uid;
//        String sid = (String) session.get(PGMacro.SID);
//        PGException.Assert(sid != null, PGError.INVALID_SESSION, "Empty session");
//        
//        String uid = (String) session.get(PGMacro.UID);
//        String actUid = this.activeUsers.getUID(sid);
//        
//        PGException.Assert((uid != null) && (uid.equals(actUid)),
//                PGError.INVALID_SESSION, "Invalid session");
//        
//        return actUid;
    }
    
    private long convertForCheat(long realNow, Map<String, Object> data)
    {
//        if (data.containsKey("cheat"))
//        {
//            try {
//                Map<String, Object> cheatData = (Map<String, Object>) data.get("cheat");
//                String uid = (String) cheatData.get(PGMacro.UID);
//                
//                Target master = targetResolver.getMasterTarget();
//                Request req = Request.makeAMF(null, Methods.Global.GET_HACK_TIME,
//                        cheatData, realNow);
//                
//                long dTime = (Long) master.doAMF(req);
//                
//                return realNow + dTime;
//            } catch (InvocationTargetException ex) {
//            }
//        }
        
        return realNow;
    }
}
