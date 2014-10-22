/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import java.util.Map;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class CFPayment extends JSONMapString<CFPayment.Payment>
{
    private CFPayment() {}
    public static CFPayment parse(Map<String, Object> data)
    {
        CFPayment payment = new CFPayment();
        payment.deser(data);
        
        return payment;
    }
    
    @Override
    protected Payment newElement(Map<String, Object> elemJSON) {
        return new Payment();
    }
    
    public static class Payment implements JSONable
    {
        private Payment() {}
        
        private int zingCoin;
        private int gameCoin;
        private String name;
        
        @Override
        public void deser(Map<String, Object> json) {
            this.zingCoin = PGHelper.toInteger((String) json.get("zing"));
            this.gameCoin = PGHelper.toInteger((String) json.get("coin"));
            this.name = (String) json.get("name");
        }

        public int getZingCoin() {
            return zingCoin;
        }

        public int getGameCoin() {
            return gameCoin;
        }
        
        public String getName() {
            return name;
        }
    }
}
