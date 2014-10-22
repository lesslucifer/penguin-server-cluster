/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.prize;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import pgentity.EntityContext;

/**
 *
 * @author KieuAnh
 */
class PrizePool extends HashMap<String, IPrizeFactory>{
    private PrizePool()
    {
        super();
        
        this.put("gold", GoldPrize.Factory.inst());
        this.put("coin", CoinPrize.Factory.inst());
        this.put("exp", ExpPrize.Factory.inst());
        this.put("fish", FishPrize.Factory.inst());
        this.put("eggs", EggsPrize.Factory.inst());
        this.put("penguin", PenguinPrize.Factory.inst());
        this.put("add_turn", RPTurnPrize.Factory.inst());
    }
    
    private static final PrizePool inst = new PrizePool();
    
    public static PrizePool inst()
    {
        return inst;
    }
    
    public PGPrize createPrize(String prizeType, Object prizeData)
    {
        if (this.containsKey(prizeType))
        {
            return this.get(prizeType).createPrize(prizeData);
        }
        
        return NothingPrize.inst();
    }
    
    private static class NothingPrize implements PGPrize
    {
        private NothingPrize()
        {
            super();
        }
        
        private static final NothingPrize inst = new NothingPrize();
        
        public static NothingPrize inst()
        {
            return inst;
        }
        
        @Override
        public Map<String, Object> award(EntityContext context, long now) {
            return Collections.EMPTY_MAP;
        }

        @Override
        public boolean canPrize(EntityContext prize)
        {
            return true;
        }
    }
}