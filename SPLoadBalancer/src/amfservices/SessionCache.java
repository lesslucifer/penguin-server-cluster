/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package amfservices;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author KieuAnh
 */
class SessionCache
{
    private static final SessionCache inst = new SessionCache();
    
    public static SessionCache inst()
    {
        return inst;
    }
    private static final long EXPIRE_TIME = 30;
    private final Cache<String, String> cache;
    
    private SessionCache()
    {
        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(EXPIRE_TIME, TimeUnit.MINUTES)
                .build();
    }
    
    public String getUID(String session)
    {
        return cache.getIfPresent(session);
    }
    
    public boolean isValid(String session)
    {
        return cache.getIfPresent(session) != null;
    }
    
    public void putSession(String session, String uid)
    {
        cache.put(session, uid);
    }
}