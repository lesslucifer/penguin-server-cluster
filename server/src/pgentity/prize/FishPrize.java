/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.prize;

import java.util.Collections;
import java.util.Map;
import pgentity.EntityContext;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class FishPrize implements PGPrize{
    private int nFish;

    private FishPrize(int nGoldnFish) {
        this.nFish = nGoldnFish;
    }
    
    @Override
    public Map<String, Object> award(EntityContext context, long now)
    {
        context.getUser().setFish(context.getUser().getFish() + nFish);
        return Collections.EMPTY_MAP;
    }

    @Override
    public boolean canPrize(EntityContext context)
    {
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
            return new FishPrize(PGHelper.toInteger(prizeData));
        }
    }
}
