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
public enum QuestState
{
    INVALID(-1, "invalid"),
    LOCKED(0, "locked"),
    NEW(1, "new"),
    ACCEPTED(2, "accepted"),
    COMPLETED(3, "completed"),
    RETURNED(4, "returned");

    private final int value;
    private final String description;
    private QuestState(int state, String desc)
    {
        this.value = state;
        this.description = desc;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString()
    {
        return this.description;
    }

    private static final Map<Integer, QuestState> allStates = new HashMap();
    static
    {
        for (QuestState questState : QuestState.values()) {
            allStates.put(questState.value, questState);
        }
    }
    public static QuestState get(int st)
    {
        return allStates.get(st);
    }
}
