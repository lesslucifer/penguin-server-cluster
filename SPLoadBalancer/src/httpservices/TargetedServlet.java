/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package httpservices;

import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.*;
import share.PGMacro;
import target.RMITargetResolver;
import target.Request;
import target.Target;
import target.TargetResolver;

/**
 *
 * @author KieuAnh
 */
public class TargetedServlet extends SERVLET.ServerServlet
{
    private final TargetResolver targetResolver = RMITargetResolver.inst();
    private final String method;

    public TargetedServlet(String method) {
        this.method = method;
    }
    
    @Override
    protected void doProcess(HttpServletRequest req, HttpServletResponse resp)
    {
        try {
            final long now = System.currentTimeMillis();
            final String uid = extractUID(req);
            Request request = Request.makeHTTP(uid, method, req, now);
            Target target = targetResolver.getUserTarget(uid);
            Object targetResp = target.doHTTP(request);
            
            out(targetResp, resp);
            super.doProcess(req, resp);
        } catch (InvocationTargetException ex) {
            out(ex.getTargetException().toString(), resp);
        }
    }
    
    protected String extractUID(HttpServletRequest req)
    {
        return req.getParameter(PGMacro.UID);
    }
}
