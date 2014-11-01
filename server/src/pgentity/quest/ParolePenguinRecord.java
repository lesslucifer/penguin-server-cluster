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
public class ParolePenguinRecord implements QuestRecord {
    private final int penguinLevel;

    public ParolePenguinRecord(int penguinLevel) {
        this.penguinLevel = penguinLevel;
    }

    public int getPenguinLevel() {
        return penguinLevel;
    }
}
