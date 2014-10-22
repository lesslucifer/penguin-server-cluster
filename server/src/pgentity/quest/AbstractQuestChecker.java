/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import pgentity.CheckerLogPool;

/**
 *
 * @author KieuAnh
 */
abstract class AbstractQuestChecker implements QuestChecker
{
    private final CheckerLogPool logPool;

    protected AbstractQuestChecker(CheckerLogPool logPool) {
        this.logPool = logPool;
    }

    protected CheckerLogPool getLogPool() {
        return logPool;
    }
}
