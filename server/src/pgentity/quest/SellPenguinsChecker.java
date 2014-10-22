/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import pgentity.EntityContext;
import pgentity.Penguin;
import share.PGHelper;

/**
 * Not implemented
 * @author KieuAnh
 */
public class SellPenguinsChecker implements QuestChecker
{
    Map<String, SellPenguinRequire> requires;
    
    public SellPenguinsChecker(Map<String, Object> need)
    {
        requires = Maps.transformValues(need,
            new Function<Object, SellPenguinRequire>()
            {
                @Override
                public SellPenguinRequire apply(Object data) {
                    Map<String, Object> mData = (Map) data;
                    return new SellPenguinRequire(
                            PGHelper.toInteger((String) mData.get("number")),
                            PGHelper.toInteger((String) mData.get("level")));
                }
            });
    }
    
    @Override
    public boolean isAccept(EntityContext context) {
        Set<String> penguinIDs = context.getCote().penguins().getAll();
        
        Map<String, Integer> nPenguinsOK = Maps.transformValues(requires, 
            new Function<SellPenguinRequire, Integer>()
            {
                @Override
                public Integer apply(SellPenguinRequire f) {
                    return 0;
                }
            });
        
        for (String penguinID : penguinIDs) {
            Penguin penguin = Penguin.getPenguin(context.getUid(),
                    context.getCoteID(), penguinID);
            
            if (requires.containsKey(penguin.getKind()) &&
                penguin.getLevel() >= requires.get(penguin.getKind()).level())
            {
                nPenguinsOK.put(penguin.getKind(), nPenguinsOK.get(penguin.getKind()) + 1);
            }
        }
        
        for (String pKind : requires.keySet()) {
            if (nPenguinsOK.get(pKind) < requires.get(pKind).number())
            {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public void returnQuest(EntityContext context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Object> buildAMF(EntityContext context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Object> dump() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static class SellPenguinRequire
    {
        private final int nPenguin, lvlPenguin;

        public SellPenguinRequire(int nPenguin, int lvlPenguin) {
            this.nPenguin = nPenguin;
            this.lvlPenguin = lvlPenguin;
        }

        public int number() {
            return nPenguin;
        }

        public int level() {
            return lvlPenguin;
        }
    }
}
