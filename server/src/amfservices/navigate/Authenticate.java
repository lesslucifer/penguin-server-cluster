/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package amfservices.navigate;

import config.PGConfig;
import db.PGKeys;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import libCore.SNServices;
import share.PGLog;
import org.apache.commons.codec.digest.DigestUtils;
import pgentity.UserList;
import pgentity.pool.EntityPool;
import share.PGError;
import share.PGException;
import share.PGHelper;
import share.PGMacro;
import zme.api.exception.ZingMeApiException;

/**
 *
 * @author KieuAnh
 */
class Authenticate {
    private Authenticate() {}
    
    public static Map<String, Object> authenticate(SessionCache sessions, Map<String, Object> params)
    {
        try
        {
            Map<String, Object> data = (Map) params.get("data");

            String uid = (String) data.get(PGMacro.UID);
            String sid = (String) data.get(PGMacro.SID);
            String signedReq = (String) data.get(PGMacro.SIGNED_REQUEST);

            SNServices zing = new SNServices(signedReq);
            PGException.Assert(uid.equals(zing.getUserInfo().getUid()),
                    PGError.INVALID_USER, "Invalid signed request");

            sessions.putSession(sid, uid);
        }
        catch (IOException ex) {
            PGException.pgThrow(ex);
        } catch (ZingMeApiException ex) {
            PGException.pgThrow(ex);
        } finally
        {
            EntityPool.inst().releaseAllThreadResources();
            PGLog.info("authenticate release resources");
        }
        
        return Collections.EMPTY_MAP;
    }
    
    public static Map<String, Object> authenticateSystem(SessionCache sessions, Map<String, Object> params)
    {
        String sid = null;
        try
        {
            Map<String, Object> data = (Map) params.get("data");

            String uid = (String) data.get(PGMacro.UID);
            String adminPass = (String) data.get(PGMacro.SIGNED_REQUEST);

            PGException.Assert(PGConfig.inst().temp().AdminTool_UID().equals(uid) ||
                    UserList.getList(UserList.ListType.SYSTEM_ACCOUNT).contains(uid),
                    PGError.INVALID_USER, "User " + uid + " not be system account");
            
            String md5Pass = DigestUtils.md5Hex(adminPass);
            PGException.Assert(PGConfig.inst().temp().SystemPasswordMD5().equals(md5Pass),
                    PGError.INVALID_SIGNED_REQUEST, "User password are wrong!");

            sid = PGHelper.randomGUID();
            sessions.putSession(sid, uid);
        }
        finally
        {
            EntityPool.inst().releaseAllThreadResources();
            PGLog.info("authenticateSystem release resources");
        }

        Map<String, Object> resp = new HashMap(1);
        Map<String, Object> data = new HashMap(1);
        data.put(PGMacro.SID, sid);
        resp.put("data", data);
        return resp;
    }
}
