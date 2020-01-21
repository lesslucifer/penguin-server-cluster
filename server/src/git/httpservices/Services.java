/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.httpservices;

import java.util.Map;
import pgentity.UserList;
import share.PGMacro;
import target.Request;

/**
 *
 * @author KieuAnh
 */
public class Services {
    public Services()
    {
        super();
    }
    
    private static final Services inst = new Services();
    
    public static Services inst()
    {
        return inst;
    }
    
    public Object whitelist(Object par)
    {
        Map<String, Object> params = (Map) par;
        String uid = ((String[]) params.get(PGMacro.UID))[0];
        return UserList.getList(UserList.ListType.WHITE_LIST)
                .contains(uid);
    }
    
    public Object BillingItem(Request req)
    {
        return null;
    }
    
    public Object ZingBilling(Request req)
    {
        return null;
    }
    
    public Object ZingController(Request req)
    {
        return null;
    }
}