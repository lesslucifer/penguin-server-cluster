/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.prize;

import java.util.Collections;
import java.util.Map;
import pgentity.EntityContext;
import pgentity.UserTempData;
import share.PGHelper;
import share.PGMacro;

/**
 *
 * @author KieuAnh
 */
public class RPTurnPrize implements PGPrize {
    private final int nTurn;

    public RPTurnPrize(int nTurn) {
        this.nTurn = nTurn;
    }
    
    @Override
    public Map<String, Object> award(EntityContext context, long now) {
        UserTempData uTemp = UserTempData.getTempData(context.getUid());
        uTemp.incData(PGMacro.RAND_PRIZE_TURN, nTurn);
        
        return Collections.EMPTY_MAP;
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
            return new RPTurnPrize(PGHelper.toInteger(prizeData));
        }   
    }
}
