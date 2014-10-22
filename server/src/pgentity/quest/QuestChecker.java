/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import pgentity.EntityContext;
import java.util.Map;

/**
 *
 * @author KieuAnh
 */
public interface QuestChecker
{
    boolean isAccept(EntityContext context);
    void returnQuest(EntityContext context);
    
    Map<String, Object> buildAMF(EntityContext context);
    
    Map<String, Object> dump();
    void destroy();
}