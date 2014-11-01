/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.quest;

/**
 *
 * @author Salm
 */
public class CollectEggsRecord implements BasicRecord
{
    private final int nEggs;

    public CollectEggsRecord(int nEggs) {
        this.nEggs = nEggs;
    }
    
    @Override
    public int getValue() {
        return nEggs;
    }
}
