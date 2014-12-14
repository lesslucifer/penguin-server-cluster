/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import pgentity.CheckerLogPool;
import pgentity.EntityContext;

/**
 *
 * @author KieuAnh
 */
class QuestCheckerPool extends HashMap<String, QuestCheckerFactory> {
    private QuestCheckerPool()
    {
        super();
        
        this.put("take_snapshot", TakeSnapshotChecker.Factory.inst());
        this.put("penguin_ate", NumberFishFedChecker.Factory.inst());
        this.put("n_penguin_feed", NumberPenguinFedChecker.Factory.inst());
        this.put("user_level", UserLevelChecker.Factory.inst());
        this.put("breed_penguin", BreedPenguinChecker.Factory.inst());
        this.put("cote_level", CoteLevelChecker.Factory.inst());
        this.put("n_feed", BasicChecker.getFactory("n_feed"));
        this.put("n_theft_eggs", BasicChecker.getFactory("n_theft_eggs"));
        this.put("n_help_friend", BasicChecker.getFactory("n_help_friend"));
        this.put("n_sell_eggs", BasicChecker.getFactory("n_sell_eggs"));
        this.put("n_expense_gold", BasicChecker.getFactory("n_expense_gold"));
        this.put("n_visit_friend", BasicChecker.getFactory("n_visit_friend"));
        this.put("sell_egg", SellEggsChecker.Factory.inst());
        this.put("login_day", LoginDayChecker.Factory.inst());
        this.put("n_stolen_eggs", BasicChecker.getFactory("n_stolen_eggs"));
        this.put("parole_penguin", BasicChecker.getFactory("parole_penguin"));
        this.put("collect_egg", BasicChecker.getFactory("collect_egg"));
        this.put("n_feed_fish", BasicChecker.getFactory("n_feed_fish"));
        this.put("n_penguin_lv10", BasicChecker.getFactory("n_penguin_lv10"));
        this.put("n_gold_dial", BasicChecker.getFactory("n_gold_dial"));
        this.put("n_main_quest_completed", BasicChecker.getFactory("n_main_quest_completed"));
    }
    
    private static final QuestCheckerPool inst = new QuestCheckerPool();
    
    public static QuestCheckerPool inst()
    {
        return inst;
    }
    
    public QuestChecker createQuestChecker(String type, Object data, CheckerLogPool logPool)
    {
        if (this.containsKey(type))
        {
            return this.get(type).createChecker(data, logPool);
        }
        
        return AlwaysAcceptChecker.inst();
    }
    
    private static class AlwaysAcceptChecker implements QuestChecker, QuestCheckerFactory
    {
        private AlwaysAcceptChecker()
        {
            super();
        }

        private static final AlwaysAcceptChecker inst = new AlwaysAcceptChecker();

        public static AlwaysAcceptChecker inst()
        {
            return inst;
        }
        
        @Override
        public boolean isAccept(EntityContext context)
        {
            return true;
        }

        @Override
        public Map<String, Object> buildAMF(EntityContext context) {
            return Collections.EMPTY_MAP;
        }

        @Override
        public QuestChecker createChecker(Object data, CheckerLogPool logPool)
        {
            return this;
        }

        @Override
        public void returnQuest(EntityContext context)
        {
        }

        @Override
        public Map<String, Object> dump() {
            return Collections.EMPTY_MAP;
        }

        @Override
        public void destroy() {
        }
    }
}
