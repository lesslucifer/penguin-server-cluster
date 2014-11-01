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
public class GoldDialRecord implements BasicRecord {
    private final int nGold;

    public GoldDialRecord(int nGold) {
        this.nGold = nGold;
    }

    @Override
    public int getValue() {
        return nGold;
    }
}
