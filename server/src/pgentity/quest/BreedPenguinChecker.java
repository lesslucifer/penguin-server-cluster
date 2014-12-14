/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import share.PGLog;
import pgentity.CheckerLogPool;
import pgentity.EntityContext;
import pgentity.Penguin;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class BreedPenguinChecker implements QuestChecker
{
    private int nPenguinRequire = 0;
    private int reqLevel = 0;
    
    private BreedPenguinChecker(Map<String, String> data)
    {
        this.nPenguinRequire = PGHelper.toInteger(data.get("number"));
        this.reqLevel = PGHelper.toInteger(data.get("level"));
    }
    
    @Override
    public boolean isAccept(EntityContext context)
    {
        if (context.getCote().penguins().size() >= nPenguinRequire)
        {
            Set<String> penguinIDs = context.getCote().penguins().getAll();
            
            return this.nSuccessPenguins(context.getUid(),
                    context.getCoteID(), penguinIDs) >= nPenguinRequire;
        }
        
        return false;
    }

    @Override
    public Map<String, Object> buildAMF(EntityContext context)
    {
        Set<String> penguinIDs = context.getCote().penguins().getAll();
        
        Map<String, Object> data = new HashMap();
        data.put("breed_penguin", this.nSuccessPenguins(context.getUid(),
                context.getCoteID(), penguinIDs));
        return data;
    }
    
    private int nSuccessPenguins(String uid, String coteID,
            Iterable<String> penguinIDs)
    {
        int nSuccessPenguin = 0;
        for (String penguinID : penguinIDs) {
            Penguin penguin = Penguin.getPenguin(uid, coteID, penguinID);

            if (penguin.getLevel() >= reqLevel)
            {
                ++nSuccessPenguin;
            }
        }
        
        return nSuccessPenguin;
    }

    @Override
    public void returnQuest(EntityContext context)
    {
        PGLog.debug("Return quest breed %d penguin at level %d",
                nPenguinRequire, reqLevel);
    }

    @Override
    public Map<String, Object> dump() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public void destroy() {
    }
    
    public static class Factory implements QuestCheckerFactory
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
        public QuestChecker createChecker(Object data, CheckerLogPool logPool) {
            return new BreedPenguinChecker((Map) data);
        }
    }
}
