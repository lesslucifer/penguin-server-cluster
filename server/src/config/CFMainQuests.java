/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * @author KieuAnh
 */
public class CFMainQuests extends JSONMapString<CFMainQuests.QuestLine>
{
    private CFMainQuests() {}
    public static CFMainQuests parse(Map<String, Object> json)
    {
        CFMainQuests mainQuest = new CFMainQuests();
        mainQuest.deser(json);
        
        return mainQuest;
    }

    @Override
    protected QuestLine newElement(Map<String, Object> elemJSON) {
        return new QuestLine();
    }

    @Override
    public void deser(Map<String, Object> json) {
        super.deser((Map) json.get("data"));
    }

    public static class QuestLine implements JSONable
    {
        private QuestLine() {}
        private final NavigableMap<Integer, QuestLevel> qLevels = new TreeMap();

        @Override
        public void deser(Map<String, Object> json)
        {
            for (Map.Entry<String, Object> qLineEntry : json.entrySet()) {
                Integer qLevel = Integer.valueOf(qLineEntry.getKey());
                Map<String, Object> qLevelJSON = (Map) qLineEntry.getValue();
                
                QuestLevel questLevel = new QuestLevel();
                questLevel.deser(qLevelJSON);
                this.qLevels.put(qLevel, questLevel);
            }
        }
        
        public QuestLevel get(int uLevel)
        {
            return this.qLevels.get(this.qLevels.floorKey(uLevel));
        }
        
        public int minimizeLevel(int uLevel)
        {
            return this.qLevels.floorKey(uLevel);
        }

        public static class QuestLevel extends JSONMapArray<QuestLevel.Quest>
        {
            int i = 0;
            private QuestLevel() {}
            
            @Override
            protected Quest newElement(Map<String, Object> elemJSON) {
                return new Quest();
            }

            public static class Quest implements JSONable
            {
                private Quest() {}

                private ImmutableMap<String, Object> need;
                private ImmutableMap<String, Object> prize;

                @Override
                public void deser(Map<String, Object> json)
                {
                    this.need = ImmutableMap.copyOf(
                            (Map<String, Object>) json.get("need"));
                    this.prize = ImmutableMap.copyOf(
                            (Map<String, Object>) json.get("prize"));
                }

                public ImmutableMap<String, Object> getNeed() {
                    return need;
                }

                public ImmutableMap<String, Object> getPrize() {
                    return prize;
                }
            }
        }
    }
}