/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.racing;

import pgentity.quest.QuestRecord;

/**
 *
 * @author Salm
 */
public interface RaceRecord extends QuestRecord {
    String uid();
    Number value();
    long time();
}
