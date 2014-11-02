/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.racing;

import java.util.HashMap;
import java.util.Map;
import pgentity.quest.QuestLogger;

/**
 *
 * @author Salm
 */
class RaceLoggerFactory {
    private RaceLoggerFactory() {}
    
    private static final Map<String, Class<? extends RaceRecord>> loggers;
    
    static
    {
        loggers = new HashMap();
        loggers.put("race_exp_gained", ExpGainedRecord.class);
    }
    
    public static Class<? extends RaceRecord> getRecordType(String cri)
    {
        return loggers.get(cri);
    }
    
    public static QuestLogger getLogger(Race race)
    {
        return Ranking.getRanking(race);
    }
}
