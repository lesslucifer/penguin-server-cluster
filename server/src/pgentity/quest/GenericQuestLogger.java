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
abstract class GenericQuestLogger<E extends QuestRecord> implements QuestLogger
{
    @Override
    public void log(QuestRecord record)
    {
        try
        {
            E eRecord = (E) record;
            genLog(eRecord);
        }
        catch (ClassCastException ex)
        {
            // ignore that record
        }
    }
    
    protected abstract void genLog(E record);
}
