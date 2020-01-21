/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package amfservices;

import java.util.Map;

/**
 *
 * @author KieuAnh
 */
public interface Reflector {

    //========================= AUTHENTICATION =============================
    Map<String, Object> authenticate(Map<String, Object> params);

    Map<String, Object> authenticateSystem(Map<String, Object> params);

    Map<String, Object> reflectCall(String method, Map<String, Object> params);
    
}
