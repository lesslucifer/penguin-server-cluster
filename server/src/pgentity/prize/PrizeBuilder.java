/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.prize;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import pgentity.EntityContext;
import share.PGError;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
class PrizeBuilder
{
    private Set<PGPrize> prizes = new HashSet();
    
    public void addPrize(PGPrize prize)
    {
        this.prizes.add(prize);
    }
    
    public PGPrize getPrize()
    {
        CompositeQuestPrize composite = new CompositeQuestPrize();
        for (PGPrize qp : prizes) {
            composite.add(qp);
        }
        
        return composite;
    }
    
    private static class CompositeQuestPrize extends HashSet<PGPrize> implements PGPrize
    {
        @Override
        public Map<String, Object> award(EntityContext context, long now)
        {
            PGException.Assert(this.canPrize(context),
                    PGError.CANNOT_PRIZE, "Cannot prizing");
            
            Map<String, Object> data = new HashMap(this.size());
            for (PGPrize qp : this) {
                Map<String, Object> pzData = qp.award(context, now);
                data.putAll(pzData);
            }
            
            return data;
        }

        @Override
        public boolean canPrize(EntityContext context)
        {
            for (PGPrize prize : this) {
                if (!prize.canPrize(context))
                {
                    return false;
                }
            }
            
            return true;
        }
    }
}
