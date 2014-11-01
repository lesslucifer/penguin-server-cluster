/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.prize;

import java.util.Map;

/**
 *
 * @author KieuAnh
 */
public class PrizeFactory
{    
    public static PGPrize getPrize(Map<String, Object> prize)
    {
        PrizeBuilder prizeBuilder = new PrizeBuilder();
        
        if (prize != null)
        {
            for (Map.Entry<String, Object> prizeEntry : prize.entrySet()) {
                String prizeType = prizeEntry.getKey();
                Object prizeData = prizeEntry.getValue();

                prizeBuilder.addPrize(PrizePool.inst().createPrize(prizeType, prizeData));
            }
        }
        
        return prizeBuilder.getPrize();
    }
}
