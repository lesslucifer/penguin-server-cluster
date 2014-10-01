/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.reflect;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import libCore.SNServices;
import share.PGError;
import share.PGException;
import share.PGMacro;
import zme.api.exception.ZingMeApiException;

/**
 *
 * @author KieuAnh
 */
class Authenticate {
    private Authenticate() {}
    
    public static Map<String, Object> authenticate(SessionCache sessions,
            Map<String, Object> params)
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
        }
        
        return Collections.EMPTY_MAP;
    }
    /*
    public static Map<String, Object> authenticateSystem(SessionCache sessions,
            TargetResolver targResl,
            Map<String, Object> params, long now)
    {
        String sid = null;
        try
        {
            Map<String, Object> data = (Map) params.get("data");

            String uid = (String) data.get(PGMacro.UID);
            String adminPass = (String) data.get(PGMacro.SIGNED_REQUEST);
            
            boolean isSystem = PGConst.ADMIN_TOOL_UID.equals(uid);
            if (!isSystem)
            {
                Target masterTarget = targResl.getMasterTarget();
                Request req = Request.makeAMF(null, Methods.Global.IN_SYSTEM_LIST,
                        AMFBuilder.make(PGMacro.UID, uid), now);
                isSystem = (Boolean) masterTarget.doAMF(req);
            }
            
            PGException.Assert(isSystem,
                    PGError.INVALID_USER, "User " + uid + " not be system account");
            
            
            PGException.Assert(PGConst.ADMIN_TOOL_PASSWORD.equals(adminPass),
                    PGError.INVALID_SIGNED_REQUEST, "User password are wrong!");

            sid = PGHelper.randomGUID();
            sessions.putSession(sid, uid);
        } catch (InvocationTargetException ex) {
            PGException.pgThrow(ex);
        }

        Map<String, Object> resp = new HashMap(1);
        Map<String, Object> data = new HashMap(1);
        data.put(PGMacro.SID, sid);
        resp.put("data", data);
        return resp;
    }
    */
}
