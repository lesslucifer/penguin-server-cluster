/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import db.DBContext;
import java.util.HashMap;
import java.util.Map;
import share.PGLog;
import pgentity.CheckerLogPool;
import pgentity.EntityContext;
import share.PGHelper;
import share.AMFBuilder;

/**
 *
 * @author KieuAnh
 */
class NumberPenguinFedChecker extends AbstractQuestChecker
{
    private int nReq;

    private NumberPenguinFedChecker(int nReq, CheckerLogPool logPool) {
        super(logPool);
        this.nReq = nReq;
    }

    @Override
    public boolean isAccept(EntityContext context) {
        int fed = DBContext.Redis().scard(
                getLogPool().getKey("penguins_fed")).intValue();
        return fed >= nReq;
    }

    @Override
    public Map<String, Object> buildAMF(EntityContext context)
    {
        Map<String, Object> data = new HashMap(1);
        data.put("n_penguin_feed", AMFBuilder.toAMF(
                DBContext.Redis().smembers(getLogPool().getKey("penguins_fed"))));
        
        return data;
    }

    @Override
    public void returnQuest(EntityContext context)
    {
        PGLog.debug("Return quest fed %d distinct penguin", this.nReq);
        this.destroy();
    }

    @Override
    public Map<String, Object> dump() {
        Map<String, Object> data = new HashMap(1);
        data.put("n_penguin_feed", 
                DBContext.Redis().smembers(getLogPool().getKey("penguins_fed")));
        
        return data;
    }

    @Override
    public void destroy() {
        DBContext.Redis().del(getLogPool().getKey("penguins_fed"));
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
        public QuestChecker createChecker(Object data, CheckerLogPool logPool)
        {
            return new NumberPenguinFedChecker(PGHelper.toInteger(data), logPool);
        }
    }
}