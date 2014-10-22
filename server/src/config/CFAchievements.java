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
public class CFAchievements extends JSONMapString<CFAchievements.Achievement>
{
    private CFAchievements() {}
    public static CFAchievements parse(Map<String, Object> json)
    {
        CFAchievements achivements = new CFAchievements();
        achivements.deser(json);
        
        return achivements;
    }

    @Override
    protected Achievement newElement(Map<String, Object> elemJSON) {
        return new Achievement();
    }

    @Override
    public void deser(Map<String, Object> json) {
        super.deser((Map) json.get("data"));
    }
    
    public static class Achievement extends JSONMapString<Achievement.Medal>
    {
        private String action;
        private boolean enable;

        @Override
        protected Medal newElement(Map<String, Object> elemJSON) {
            return new Medal();
        }

        @Override
        public void deser(Map<String, Object> json) {
            this.action = (String) json.get("action");
            this.enable = (Boolean) json.get("enable");
            super.deser((Map) json.get("medals"));
        }

        public String getAction() {
            return action;
        }

        public boolean isEnable() {
            return enable;
        }
        
        public static class Medal implements JSONable
        {
            private Object require;
            private Map<String, Object> prize;
            
            private Medal() {}
            
            @Override
            public void deser(Map<String, Object> json) {
                this.require = json.get("require");
                this.prize = (Map) json.get("prize");
            }  

            public Object getRequire() {
                return require;
            }

            public Map<String, Object> getPrize() {
                return prize;
            }
        }
    }
}
