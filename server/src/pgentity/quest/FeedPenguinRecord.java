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
public class FeedPenguinRecord implements QuestRecord
{
    private final String penguinID;
    private final int nFish;

    public FeedPenguinRecord(String penguinID, int nFish) {
        this.penguinID = penguinID;
        this.nFish = nFish;
    }

    public String getPenguinID() {
        return penguinID;
    }

    public int getnFish() {
        return nFish;
    }
}
