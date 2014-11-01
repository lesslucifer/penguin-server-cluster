/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import share.PGLog;
import pgentity.CheckerLogPool;
import pgentity.EntityContext;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class CoteLevelChecker implements QuestChecker
{
    private final int reqLevel;

    public CoteLevelChecker(int reqLevel) {
        this.reqLevel = reqLevel;
    }
    
    @Override
    public boolean isAccept(EntityContext context)
    {
        return context.getCote().getLevel() >= reqLevel;
    }

    @Override
    public Map<String, Object> buildAMF(EntityContext context)
    {
        Map<String, Object> data = new HashMap();
        data.put("cote_level", context.getCote().getLevel());
        return data;
    }

    @Override
    public void returnQuest(EntityContext context)
    {
        PGLog.debug("Return quest cote level %d", this.reqLevel);
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
            return new CoteLevelChecker(PGHelper.toInteger(data));
        }
    }
}
