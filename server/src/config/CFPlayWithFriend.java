/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import java.util.Map;

/**
 *
 * @author KieuAnh
 */
public class CFPlayWithFriend implements JSONable
{
    private CFPlayWithFriend() {}
    static CFPlayWithFriend parse(Map<String, Object> json)
    {
        CFPlayWithFriend f = new CFPlayWithFriend();
        f.deser(json);
        
        return f;
    }
    
    private FriendData helpFish;
    private FriendData stealEgg;

    @Override
    public void deser(Map<String, Object> json)
    {
        this.helpFish = new FriendData();
        this.getHelpFish().deser((Map<String, Object>) json.get("help_fish"));
        
        this.stealEgg = new FriendData();
        this.getStealEgg().deser((Map<String, Object>) json.get("steal_egg"));
    }

    /**
     * @return the helpFish
     */
    public FriendData getHelpFish() {
        return helpFish;
    }

    /**
     * @return the stealEgg
     */
    public FriendData getStealEgg() {
        return stealEgg;
    }
    
    public static class FriendData implements JSONable
    {
        protected  FriendData() {}
        private int perFriend;
        private int maxFriend;

        @Override
        public void deser(Map<String, Object> json)
        {
            this.perFriend = Integer.parseInt(json.get("per_friend").toString());
            this.maxFriend = Integer.parseInt(json.get("max_friend").toString());
        }

        public int getPerFriend() {
            return perFriend;
        }

        public int getMaxFriend() {
            return maxFriend;
        }
    }
}