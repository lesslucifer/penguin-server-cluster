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
public class DropFish_FishRecord implements BasicRecord {
    private final int nFish;

    public DropFish_FishRecord(int nFish) {
        this.nFish = nFish;
    }
    
    @Override
    public int getValue() {
        return nFish;
    }
}
