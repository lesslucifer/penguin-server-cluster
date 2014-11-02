/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author KieuAnh
 */
public class StolenEggsRecord implements BasicRecord {
    private Map<String, Number> stolenEggs;
    private int totalEggs;
    
    public StolenEggsRecord(Map<String, Number> stolen)
    {
        this.stolenEggs = new HashMap(stolen);
        totalEggs = 0;
        for (Map.Entry<String, Number> stolenEgg : stolen.entrySet()) {
            totalEggs += stolenEgg.getValue().intValue();
        }
    }
    
    public StolenEggsRecord(String kind, int nEgg)
    {
        this.stolenEggs = new HashMap(1);
        stolenEggs.put(kind, nEgg);
        this.totalEggs = nEgg;
    }

    public Map<String, Number> getStolenEggs() {
        return stolenEggs;
    }

    public int getTotalEggs() {
        return totalEggs;
    }

    @Override
    public int getValue() {
        return totalEggs;
    }
}