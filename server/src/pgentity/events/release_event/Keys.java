/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.events.release_event;

import db.RedisKey;

/**
 *
 * @author Salm
 */
class Keys {
    private Keys() {}
    
    public static final RedisKey ROOT = RedisKey.root().getChild("re");
    public static final RedisKey USER = ROOT.getHashChild("user");
    
    public static final String FD_EVENT = "event";
}
