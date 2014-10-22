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
public class ExpenseGoldRecord implements BasicRecord
{
    private final int nGoldExpense;

    public ExpenseGoldRecord(int nGoldExpense) {
        this.nGoldExpense = nGoldExpense;
    }

    @Override
    public int getValue() {
        return this.nGoldExpense;
    }
}
