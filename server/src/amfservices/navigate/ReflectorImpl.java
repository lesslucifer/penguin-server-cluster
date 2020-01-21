/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package amfservices.navigate;

import amfservices.Reflector;
import amfservices.actions.ServiceReflectTarget;
import config.PGConfig;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import libCore.config.Config;
import share.PGLog;
import org.apache.log4j.Logger;
import pgentity.HackEntity;
import pgentity.pool.EntityPool;
import share.PGError;
import share.PGException;
import share.PGHelper;
import share.PGMacro;

/**
 *
 * @author KieuAnh
 */
public class ReflectorImpl implements Reflector {
    
    private static final Logger LOG = Logger.getLogger(Reflector.class.getName());
    private final SessionCache activeUsers;
    private final ServiceReflectTarget services;

    public ReflectorImpl() {
        this.activeUsers = SessionCache.inst();
        this.services = new ServiceReflectTarget();
    }
    
    //========================= AUTHENTICATION =============================
    
    @Override
    public Map<String, Object> authenticate(Map<String, Object> params)
    {
        return Authenticate.authenticate(activeUsers, params);
    }
    
    @Override
    public Map<String, Object> authenticateSystem(Map<String, Object> params)
    {
        return Authenticate.authenticateSystem(activeUsers, params);
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
            
            // execute service
            Method mthd = ServiceReflectTarget.class.getMethod(method,
                    String.class, Map.class, Long.TYPE);
            
            Map<String, Object> data = Collections.EMPTY_MAP;
            if (params.get("data") instanceof Map)
            {
                data = (Map) params.get("data");
            }

            PGLog.debug("Input\r\n%s", PGHelper.obj2PrettyJSON(data));
            @SuppressWarnings("unchecked")
            Object responseData = mthd.invoke(services, uid, data, now);
            
            this.putError(null, 0, content);
            content.put("data", responseData);
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
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException ex){
            this.putError(ex, content);
        }
        finally
        {
            EntityPool.inst().releaseAllThreadResources();
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
            Map<String, Object> config = new HashMap<>();
            config.put("version", serverConfigVersion.toString());
            Map<String, Object> clientConfig = PGConfig.inst().getAllConfigs();
            config.put("config", clientConfig);

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
    {
        if (data.containsKey("cheat"))
        {
            Map<String, Object> cheatData = (Map<String, Object>) data.get("cheat");
            String uid = (String) cheatData.get(PGMacro.UID);
            
            HackEntity hacker = HackEntity.getEntity(uid);
            
            return realNow + hacker.getDeltaTime();
        }
        
        return realNow;
    }
}
