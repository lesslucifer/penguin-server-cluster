/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.prize;

import java.util.Map;
import pgentity.EntityContext;

/**
 *
 * @author KieuAnh
 */
public interface PGPrize
{
    Map<String, Object> award(EntityContext context, long now);
    boolean canPrize(EntityContext context);
}
