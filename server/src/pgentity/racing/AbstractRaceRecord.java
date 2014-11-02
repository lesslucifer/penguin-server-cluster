/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.racing;

/**
 *
 * @author Salm
 */
public class AbstractRaceRecord implements RaceRecord {
    private final String uid;
    private final Number value;
    private final long time;

    protected AbstractRaceRecord(String uid, Number value, long time) {
        this.uid = uid;
        this.value = value;
        this.time = time;
    }

    @Override
    public String uid() {
        return uid;
    }

    @Override
    public Number value() {
        return value;
    }
    
    @Override
    public long time()
    {
        return time;
    }
}
