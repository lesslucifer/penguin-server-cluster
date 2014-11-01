/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.prize;

import config.PGConfig;
import java.util.HashMap;
import java.util.Map;
import pgentity.EntityContext;
import pgentity.Penguin;
import share.PGHelper;
import db.PGKeys;
import share.PGMacro;

/**
 *
 * @author KieuAnh
 */
public class PenguinPrize implements PGPrize
{
    private String pKind;
    private int pLevel;
    
    public PenguinPrize(String pKind, int pLevel)
    {
        this.pKind = pKind;
        this.pLevel = pLevel;
    }
    
    @Override
    public Map<String, Object> award(EntityContext context, long now)
    {
        String uid = context.getUser().getUid();
        String coteID = context.getCote().getCoteID();
        String penguinID = PGKeys.randomKey();
        Penguin.newPenguin(uid, coteID, penguinID, pKind, pLevel, now);
        
        context.getCote().penguins().add(penguinID);
        context.getPenguindex().add(pKind);
        
        return buildReturnAMF(penguinID);
    }

    @Override
    public boolean canPrize(EntityContext context)
    {
        final int maxPenguin = PGConfig.inst().getCote()
                .get(context.getCote().getLevel()).getMaxPenguin();
        
        return context.getCote().penguins().size() + 1 <= maxPenguin;
    }
    
    private Map<String, Object> buildReturnAMF(String penguinID)
    {
        Map<String, Object> data = new HashMap(1);
        Map<String, Object> penguinData = new HashMap(1);
        penguinData.put(PGMacro.PENGUIN_ID, penguinID);
        data.put("penguin", penguinData);
        
        return data;
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
            Map<String, Object> pengPrizeData = (Map) prizeData;
            String pKind = (String) pengPrizeData.get("kind");
            int pLevel = PGHelper.toInteger(pengPrizeData.get("level").toString());
            
            return new PenguinPrize(pKind, pLevel);
        }
    }
}
