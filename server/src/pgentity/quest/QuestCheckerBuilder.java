/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import pgentity.EntityContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author KieuAnh
 */
class QuestCheckerBuilder
{
    private CompositeQuestChecker checker = new CompositeQuestChecker();
    
    public void addChecker(QuestChecker qChecker)
    {
        this.checker.addChecker(qChecker);
    }
    
    public QuestChecker getChecker()
    {
        return this.checker;
    }
    
    private static class CompositeQuestChecker implements QuestChecker
    {
        private Set<QuestChecker> checkers = new HashSet();

        public void addChecker(QuestChecker checker)
        {
            this.checkers.add(checker);
        }

        public void removeChecker(QuestChecker checker)
        {
            this.checkers.remove(checker);
        }   

        @Override
        public boolean isAccept(EntityContext context)
        {
            for (QuestChecker checker : checkers) {
                if (!checker.isAccept(context))
                {
                    return false;
                }
            }

            return true;
        }

        @Override
        public void returnQuest(EntityContext context)
        {
            for (QuestChecker checker : checkers) {
                checker.returnQuest(context);
            }
        }

        @Override
        public Map<String, Object> buildAMF(EntityContext context) {
            Map<String, Object> data = new HashMap();
            
            for (QuestChecker checker : this.checkers) {
                data.putAll(checker.buildAMF(context));
            }
            
            return data;
        }

        @Override
        public Map<String, Object> dump() {
            Map<String, Object> data = new HashMap(this.checkers.size());
            
            for (QuestChecker checker : this.checkers) {
                data.putAll((Map) checker.dump());
            }
            
            return data;
        }

        @Override
        public void destroy() {
            for (QuestChecker checker : this.checkers) {
                checker.destroy();
            }
        }
    }
}