/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.racing;

import db.PGKeys;
import pgentity.redis.zset.OrderedSet;

/**
 *
 * @author Salm
 */
public class RaceList extends OrderedSet {
    private RaceList() {
        super(PGKeys.RACING);
    }
    
    private static final RaceList inst = new RaceList();
    
    public static RaceList getRaces()
    {
        return inst;
    }
}
