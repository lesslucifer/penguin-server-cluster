/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package libCore;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import zme.api.core.Environment;
import zme.api.exception.ZingMeApiException;
import zme.api.graph.ZME_Me;
import zme.api.oauth.ZME_Authentication;

/**
 *
 * @author KieuAnh
 */
public class SNServices
{
    private static final String APP_NAME = "canhcutvuive";
    private static final String API_KEY = "ccc1c697cd284726a23fa28884ceed4a";
    private static final String SECRECT_KEY = "c13a4f0dcfae4e8daba016a37c62087b";
    private static final Environment ENV = Environment.PRODUCTION;
    
    private final String signedRequest;
    
    private String accessToken = null;
    private ZMUserInfo userInfo = null;
    
    private ZME_Authentication zmAuth = null;
    private ZME_Me zmMe = null;

    public SNServices(String signedRequest) {
        this.signedRequest = signedRequest;
    }
    
    public List<Object> getZMFriends() 
            throws ZingMeApiException, IOException
    {
        return (List) this.getZMMe().getFriends();
    }
    
    private ZME_Authentication getAuthentication()
    {
        if (this.zmAuth == null)
        {
            this.zmAuth = new ZME_Authentication(APP_NAME, API_KEY,
                SECRECT_KEY, ENV);
        }
        
        return zmAuth;
    }
    
    private ZME_Me getZMMe() throws ZingMeApiException
    {
        if (this.zmMe == null)
        {
            this.zmMe = new ZME_Me(this.getAccessToken());
        }
        
        return this.zmMe;
    }
    
    public String getAccessToken() throws ZingMeApiException
    {
        if (this.accessToken == null)
        {
            this.accessToken = this.getAuthentication()
                    .getAccessTokenFromSignedRequest(this.signedRequest);
        }
        
        return this.accessToken;
    }

    public String getSignedRequest() {
        return signedRequest;
    }
    
    public boolean validUser(String uid) throws IOException, ZingMeApiException
    {
        return uid.equals(this.getUserInfo().getUid());
    }
    
    private static final String ALL_FIELDS = "id,username,displayname,tinyurl,profile_url,gender,dob";
    private static final String FIELDS = "id,displayname,tinyurl";
    public ZMUserInfo getUserInfo() throws IOException, ZingMeApiException
    {
        if (this.userInfo == null)
        {
            this.userInfo = new ZMUserInfo(getZMMe().getInfo(FIELDS));
        }
        
        return this.userInfo;
    }
    
    public static class ZMUserInfo
    {
        private static final String K_UID = "id";
        private static final String K_DISPLAYNAME = "displayname";
        private static final String K_AVATAR = "tinyurl";
        
        private final String uid;
        private final String avatar;
        private final String displayName;

        private ZMUserInfo(String uid, String avatar, String displayName) {
            this.uid = uid;
            this.avatar = avatar;
            this.displayName = displayName;
        }
        
        private ZMUserInfo(Map<String, String> data)
        {
            this.uid = String.valueOf(data.get(K_UID));
            this.displayName = String.valueOf(data.get(K_DISPLAYNAME));
            this.avatar = String.valueOf(data.get(K_AVATAR));
        }

        public String getUid() {
            return uid;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
