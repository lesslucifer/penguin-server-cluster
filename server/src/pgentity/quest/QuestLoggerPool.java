/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import java.util.HashMap;
import java.util.Map;
import pgentity.LogPool;

/**
 *
 * @author KieuAnh
 */
class QuestLoggerPool extends HashMap<String, QuestLoggerFactory>
{
    private QuestLoggerPool()
    {
        super();
        
        this.put("take_snapshot", TakeSnapshotLogger.Factory.inst());
        this.put("penguin_ate", FeedPenguinLogger.Factory.inst());
        this.put("n_penguin_feed", FeedPenguinLogger.Factory.inst());
        this.put("n_feed", BasicLogger.getFactory(DropFishRecord.class, "n_feed"));
        this.put("n_theft_eggs", BasicLogger.getFactory(StealEggRecord.class, "n_theft_eggs"));
        this.put("n_help_friend", BasicLogger.getFactory(HelpFriendRecord.class, "n_help_friend"));
        this.put("n_sell_eggs", BasicLogger.getFactory(SellEggRecord.class, "n_sell_eggs"));
        this.put("n_expense_gold", BasicLogger.getFactory(ExpenseGoldRecord.class, "n_expense_gold"));
        this.put("n_visit_friend", BasicLogger.getFactory(VisitFriendRecord.class, "n_visit_friend"));
        this.put("login_day", LoginDayLogger.Factory.inst());
        this.put("n_stolen_eggs", BasicLogger.getFactory(StolenEggsRecord.class, "n_stolen_eggs"));
        this.put("parole_penguin", ParolePenguinLogger.Factory.inst());
        this.put("collect_egg", BasicLogger.getFactory(CollectEggsRecord.class, "collect_egg"));
        this.put("n_feed_fish", BasicLogger.getFactory(DropFish_FishRecord.class, "n_feed_fish"));
        this.put("n_penguin_lv10", BasicLogger.getFactory(PenguinMaxLevelRecord.class, "n_penguin_lv10"));
        this.put("n_gold_dial", BasicLogger.getFactory(GoldDialRecord.class, "n_gold_dial"));
        this.put("n_main_quest_completed",
                BasicLogger.getFactory(CompletedMainQuestRecord.class, "n_main_quest_completed"));
    }
    
    private static final QuestLoggerPool inst = new QuestLoggerPool();
    
    public static QuestLoggerPool inst()
    {
        return inst;
    }
    
    public QuestLogger createLogger(LogPool logPool, String type)
    {
        if (this.containsKey(type))
        {
            return this.get(type).createLogger(logPool);
        }
        
        return NothingLogger.inst();
    }
    
    private static class NothingLogger implements QuestLogger
    {
        private NothingLogger()
        {
            super();
        }
        
        private static final NothingLogger inst = new NothingLogger();
        
        public static NothingLogger inst()
        {
            return inst;
        }
        
        @Override
        public void log(QuestRecord record) {
        }

        @Override
        public void restore(Map<String, Object> data) {
        }
    }
}
