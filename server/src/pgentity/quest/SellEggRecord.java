/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

/**
 *
 * @author KieuAnh
 */
public class SellEggRecord implements BasicRecord
{
    private final int nEggSold;

    public SellEggRecord(int nEggSold) {
        this.nEggSold = nEggSold;
    }
    
    @Override
    public int getValue() {
        return nEggSold;
    }
}
