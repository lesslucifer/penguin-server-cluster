/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.events.release_event;

import db.PGKeys;
import db.RedisKey;
import pgentity.EggStore;
import pgentity.User;
import pgentity.services.EggStoreServices;
import share.AMFBuilder;
import share.PGMacro;

/**
 *
 * @author Salm
 */
class REUser {
    private final EggStore inventory;
    private REUser(User user)
    {
        this.inventory = EggStore
                .getEggStore(key(user.getUid()).getChild(PGKeys.FD_EGGS));
    }
    
    public static REUser getUser(User user)
    {
        return new REUser(user);
    }
    
    public static RedisKey key(String uid)
    {
        return Keys.ROOT.getChild(uid);
    }
    
    public EggStore inv()
    {
        return inventory;
    }
    
    public Object buildAMF()
    {
        return AMFBuilder.make(PGMacro.EGGS,
                EggStoreServices.inst().buildAMF(inventory));
    }
}
