/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.prize;

import java.util.Map;
import pgentity.Cote;
import pgentity.EntityContext;
import pgentity.services.UserServices;
import share.AMFBuilder;
import share.PGHelper;

/**
 *
 * @author Salm
 */
public class CotePrize implements PGPrize {
    private final String token;

    public CotePrize(String token) {
        this.token = token;
    }
    
    @Override
    public Map<String, Object> award(EntityContext context, long now) {
        Cote cote = UserServices.inst()
                .addNewCote(context.getUid(), token, now);
        return AMFBuilder.make("cote",
                cote.buildRescusiveAMF(true, true, true, true));
    }

    @Override
    public boolean canPrize(EntityContext context) {
        return true;
    }
    
    public static class Factory implements IPrizeFactory
    {
        private Factory()
        {
            super();
        }
        
        private static final Factory inst = new Factory();
        
        public static Factory inst()
        {
            return inst;
        }
        
        @Override
        public PGPrize createPrize(Object prizeData) {
            return new CotePrize((String) prizeData);
        }   
    }
}
