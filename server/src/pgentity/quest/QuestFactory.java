/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import java.util.Map;
import java.util.Set;
import pgentity.CheckerLogPool;
import pgentity.LogPool;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
public class QuestFactory
{
    private QuestFactory() {}
    
    public static QuestChecker getChecker(CheckerLogPool logPool, Map<String, Object> need)
    {
        QuestCheckerBuilder qcBuilder = new QuestCheckerBuilder();
        
        if (need != null)
        {
            for (Map.Entry<String, Object> needEntry : need.entrySet()) {
                String needType = needEntry.getKey();
                Object needData = needEntry.getValue();

                qcBuilder.addChecker(QuestCheckerPool.inst()
                        .createQuestChecker(needType, needData, logPool));
            }
        }
        
        return qcBuilder.getChecker();
    }
    
    public static QuestLogger getLogger(LogPool logPool, Set<String> loggers)
    {
        QuestLoggerBuilder loggerBuilder = new QuestLoggerBuilder();
        
        if (loggers != null)
        {
            for (String logger : loggers) {
                loggerBuilder.addLogger(QuestLoggerPool.inst().createLogger(logPool, logger));
            }
        }
        
        return loggerBuilder.getLogger();
    }
}
