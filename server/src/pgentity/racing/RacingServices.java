/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.racing;

import java.util.Date;
import pgentity.redis.zset.OrderedSet.ZField;
import share.PGError;
import share.PGException;
import share.TimeUtil;

/**
 *
 * @author Salm
 */
public class RacingServices {
    private RacingServices() {}
    public static final RacingServices inst = new RacingServices();
    
    public static RacingServices inst()
    {
        return inst;
    }
    
    public void addRace(String token, String name,
            Date startDay, Date endDay, String critera)
    {
        long start = TimeUtil.getMidnight(startDay.getTime()).getTime();
        long end = TimeUtil.getMidnight(endDay.getTime()).getTime();
        
        PGException.Assert(end > start, PGError.INVALID_RACE, 
                "End time must be greater than start time");
        
        RaceList races = RaceList.getRaces();
        ZField lowerRace = races.floorScore(start);
        if (lowerRace != null)
        {
            Race race = Race.getRace(lowerRace.field());
            PGException.Assert(race.getEnd() <= start,
                    PGError.OVERLAPPED_RACE,
                    "Overlapped with race " + race.getName());
        }
        
        ZField upperRace = races.ceilScore(start);
        if (upperRace != null)
        {
            Race race = Race.getRace(upperRace.field());
            PGException.Assert(end <= race.getStart(),
                    PGError.OVERLAPPED_RACE,
                    "Overlapped with race " + race.getName());
        }
        
        Race.newRace(token, name, start, end, critera);
        races.add(start, token);
        raceCache.invalidate();
    }
    
    public void updateNewDay(long now)
    {
        RaceList races = RaceList.getRaces();
        ZField justRaceField = races.floorScore(now);
        if (justRaceField != null)
        {
            Race race = Race.getRace(justRaceField.field());
            if (race.getEnd() <= now && !race.isClosed())
            {
                // prize & notify for top
                
                race.close();
                race.saveToDB();
            }
        }
        
        raceCache.invalidate();
    }
    
    private final RaceCache raceCache = new RaceCache();
    
    private static class RaceCache
    {
        private boolean validated = false;
        private Race activeRace = null;
        
        public synchronized Race getCurrentRace(long now)
        {
            if (!validated)
            {
                activeRace = loadCurrentRace(now);
            }
            
            return activeRace;
        }
        
        public synchronized void invalidate()
        {
            this.validated = false;
        }
        
        private Race loadCurrentRace(long now)
        {
            Race race = null;
            RaceList races = RaceList.getRaces();
            ZField raceField = races.floorScore(now);
            if (raceField != null)
            {
                race = Race.getRace(raceField.field());
                if (race.getStart() > now || race.getEnd() <= now)
                {
                    return null;
                }
            }

            return race;
        }
    }
}
