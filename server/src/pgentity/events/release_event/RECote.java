/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.events.release_event;

import db.PGKeys;
import db.RedisKey;
import pgentity.Cote;
import pgentity.EggStore;
import pgentity.services.EggStoreServices;
import share.AMFBuilder;
import share.PGMacro;

/**
 *
 * @author Salm
 */
class RECote {
    private final RedisKey redisKey;
    public final Cote cote;
    public final EggStore Items;

    private RECote(Cote cote) {
        this.cote = cote;
        this.redisKey = key(cote.getUid(), cote.getCoteID());
        Items = EggStore.getEggStore(redisKey.getChild(PGKeys.FD_EGGS));
    }
    
    public static RECote getCote(Cote cote)
    {
        return new RECote(cote);
    }
    
    public static RedisKey key(String uid, String coteID)
    {
        return Keys.USER.getChild(uid).getChild(coteID);
    }
    
    public Object buildAMF()
    {
        return AMFBuilder.make(PGMacro.EGGS, EggStoreServices.inst().buildAMF(Items));
    }
}
