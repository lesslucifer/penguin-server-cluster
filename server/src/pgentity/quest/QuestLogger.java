/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import java.util.Map;

/**
 *
 * @author KieuAnh
 */
public interface QuestLogger
{
    void log(QuestRecord record);
    void restore(Map<String, Object> data);
}