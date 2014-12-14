/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.racing;

import db.PGKeys;
import db.RedisKey;
import java.util.Map;
import pgentity.quest.QuestLogger;
import pgentity.quest.QuestRecord;
import pgentity.redis.zset.OrderedSet;

/**
 *
 * @author Salm
 */
class Ranking extends OrderedSet implements QuestLogger {
    private final Class<? extends RaceRecord> recordClass;
    private final long end;
    
    private Ranking(Race race) {
        super(redisKey(race.getToken()));
        this.recordClass = RaceLoggerFactory.getRecordType(race.getCriteria());
        this.end = (race.isClosed())?Long.MIN_VALUE:race.getEnd();
    }
    
    public static Ranking getRanking(Race race)
    {
        return new Ranking(race);
    }
    
    public static RedisKey redisKey(String token)
    {
        return Race.redisKey(token).getChild(PGKeys.FD_RANK);
    }

    @Override
    public void log(QuestRecord record) {
        if (!recordClass.isInstance(record))
            return;
        
        RaceRecord rrc = (RaceRecord) record;
        if (rrc.time() >= end && !this.contains(rrc.uid()))
            return;
        
        this.incrby(rrc.uid(), rrc.value());
    }

    @Override
    public void restore(Map<String, Object> data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
