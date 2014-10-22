/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import config.CFItems.Item;
import java.util.Map;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class CFItems extends JSONMapString<CFItems.Item> implements JSONable
{
    private CFItems() {}
    static CFItems parse(Map<String, Object> json)
    {
        CFItems items = new CFItems();
        items.deser(json);
        
        return items;
    }

    @Override
    protected Item newElement(Map<String, Object> elemJSON)
    {
        return new Item();
    }
    
    public static class Item implements JSONable
    {
        private Item() {}
        
        private int gold;
        private String name;
        private String type;

        @Override
        public void deser(Map<String, Object> json)
        {
            type = (String) json.get("type");
            gold = PGHelper.toInteger(json.get("gold"));
            Map<String, Object> display = (Map) json.get("display");
            name = (String) display.get("title");
        }
        
        public String getType() {
            return type;
        }

        public int getGold() {
            return gold;
        }

        public String getName() {
            return name;
        }
    }
}
