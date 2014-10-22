/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author KieuAnh
 */
public class QuestLoggerBuilder
{
    private Set<QuestLogger> loggers = new HashSet();
    
    public void addLogger(QuestLogger logger)
    {
        if (logger != null)
        {
            this.loggers.add(logger);
        }
    }
    
    public QuestLogger getLogger()
    {
        return new CompositeQuestLogger(this.loggers);
    }
    
    private static class CompositeQuestLogger extends HashSet<QuestLogger> implements QuestLogger
    {
        private CompositeQuestLogger(Set<QuestLogger> loggers)
        {
            super(loggers);
        }
        
        @Override
        public void log(QuestRecord record)
        {
            for (QuestLogger logger : this) {
                logger.log(record);
            }
        }

        @Override
        public void restore(Map<String, Object> data) {
            for (QuestLogger logger : this) {
                logger.restore(data);
            }
        }
    }
}