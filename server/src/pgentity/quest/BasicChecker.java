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

/**
 *
 * @author KieuAnh
 */
class BasicChecker extends AbstractQuestChecker
{
    private final int requirement;
    private final String field;
    
    private BasicChecker(int req, String field, CheckerLogPool logPool)
    {
        super(logPool);
        
        this.requirement = req;
        this.field = field;
    }
    
    @Override
    public boolean isAccept(EntityContext context)
    {
        return getCumulativeValue() >= requirement;
    }

    @Override
    public void returnQuest(EntityContext context)
    {
        PGLog.debug("Return basic quest with req: %d on field %s",
                this.requirement, this.field);
        this.destroy();
    }

    @Override
    public Map<String, Object> buildAMF(EntityContext context)
    {
        return (Map) dump();
    }
    
    private int getCumulativeValue()
    {
        String sCumuVal = DBContext.Redis().get(getLogPool().getKey(field));
        return PGHelper.toInteger(sCumuVal);
    }

    @Override
    public Map<String, Object> dump() {
        Map<String, Object> data = new HashMap(1);
        data.put(field, DBContext.Redis().get(getLogPool().getKey(field)));
        return data;
    }

    @Override
    public void destroy() {
        DBContext.Redis().del(getLogPool().getKey(field));
    }

    private static class Factory implements QuestCheckerFactory
    {
        private final String field;

        private Factory(String field) {
            this.field = field;
        }
        
        @Override
        public QuestChecker createChecker(Object data, CheckerLogPool logPool) {
            return new BasicChecker(PGHelper.toInteger(data), field, logPool);
        }
    }
    
    public static QuestCheckerFactory getFactory(String field)
    {
        return new BasicChecker.Factory(field);
    }
}
