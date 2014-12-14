/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import java.util.Collections;
import pgentity.EntityContext;
import java.util.HashMap;
import java.util.Map;
import share.PGLog;
import pgentity.CheckerLogPool;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class UserLevelChecker implements QuestChecker
{
    private final int reqLevel;

    private UserLevelChecker(int reqLevel) {
        this.reqLevel = reqLevel;
    }
    
    @Override
    public boolean isAccept(EntityContext context)
    {
        return context.getUser().getLevel() >= reqLevel;
    }

    @Override
    public Map<String, Object> buildAMF(EntityContext context) {
        Map<String, Object> data = new HashMap();
        data.put("user_level", context.getUser().getLevel());
        return data;
    }

    @Override
    public void returnQuest(EntityContext context)
    {
        PGLog.debug("Return quest check user level %d", reqLevel);
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
            return new UserLevelChecker(PGHelper.toInteger(data));
        }
    }
}
