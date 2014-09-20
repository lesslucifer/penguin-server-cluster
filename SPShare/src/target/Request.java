/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package target;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author KieuAnh
 */
public class Request implements Serializable {
    public static Request makeAMF(String caller, String method,
            Map<String, Object> params, long now) {
        return new Request(caller, method, now, params);
    }
    
    public static Request makeHTTP(String caller, String method,
            HttpServletRequest request, long now)
    {
        Map<String, Object> params = new HashMap(request.getParameterMap());
        return new Request(caller, method, now, params);
    }
    
    private final String caller;
    private final String method;
    private final long now;
    private final Map<String, Object> params;

    private Request(String caller, String method, long now, Map<String, Object> params) {
        this.caller = caller;
        this.method = method;
        this.now = now;
        this.params = params;
    }

    public String getCaller() {
        return caller;
    }

    public String getMethod() {
        return method;
    }

    public long getNow() {
        return now;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
