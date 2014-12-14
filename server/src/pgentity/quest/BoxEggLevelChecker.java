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
import pgentity.EntityContext;

/**
 *
 * @author KieuAnh
 */
class BoxEggLevelChecker implements QuestChecker
{
    private final int nLevelReq;

    public BoxEggLevelChecker(int nLevelReq) {
        this.nLevelReq = nLevelReq;
    }
    
    @Override
    public boolean isAccept(EntityContext context) {
        return context.getBoxEgg().getLevel() >= nLevelReq;
    }

    @Override
    public void returnQuest(EntityContext context) {
        PGLog.debug("Return quest cote level %d", this.nLevelReq);
    }

    @Override
    public Map<String, Object> buildAMF(EntityContext context) {
        Map<String, Object> data = new HashMap();
        data.put("boxegg_level", context.getCote().getLevel());
        return data;
    }

    @Override
    public Map<String, Object> dump() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public void destroy() {
    }
    
}