/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share;

/**
 * Define common macros
 * @author KieuAnh
 */
public class PGMacro
{
    private PGMacro() {}
    
    public static final String SID = "sid";
    public static final String SIGNED_REQUEST = "signed_request";
    public static final String ERROR_CODE = "error_code";
    
    // ====================== REFACTORED ========================
    public static final String UID = "uid";
    public static final String USER = "user";
    public static final String FRIEND_LIST = "friend_list";
    public static final String COIN = "coin";
    public static final String AVATAR = "avatar";
    public static final String LAST_LOGIN = "last_login";
    public static final String REPEATED_LOGIN_DAY = "repeated_login_day";
    public static class User
    {
        private User() {}
    }
    
    public static final String FID = "friend_id";
    public static final String FRIEND = "friend";
    public static final String PLAY_WITH_FRIEND = "play_with_friend";
    public static final String HELPED_FISH = "helped_fish";
    public static final String STOLEN_EGG = "stolen_egg";
    public static final String NUMBER_FRIEND_HELPED_FISH = "number_helped_fish";
    public static final String NUMBER_FRIEND_STOLEN_EGG = "number_stolen_egg";
    public static final String NPC_LIST = "npcs";
    public static class Friend
    {
        private Friend() {}
    }
    
    public static final String COTE_ID = "cote_id";
    public static final String COTE = "cote";
    public static final String PENGUIN_ID_LIST = "penguin_id_list";
    public static final String PENGUIN_LIST = "penguin_list";
    
    public static final String BOXEGG_ID = "boxegg_id";
    public static final String BOXEGG = "boxegg";
    
    public static final String PENGUIN_ID = "penguin_id";
    public static final String PENGUIN = "penguin";
    public static final String FISH_LAST_EAT = "fish_last_eat"; // redis, amf
    public static final String TIME_LAST_EAT = "time_last_eat";
    public static final String TIME_LAST_SPAWN = "time_last_spawn";
    public static final String SPAWN_LIMITED_PENGUINS = "spawn_limited";
    
    public static final String EGGS = "eggs";
    public static final String EGG_STORE = "egg_store";
    public static final String EGG_BIRTH = "egg_birth";
    public static final String FAILED_EGGS = "failed_eggs";
    public static final String SUCCESS_EGGS = "success_eggs";
    public static final String FULL_INVENTORY_EGGS = "full_inventory_eggs";
    
    public static final String ITEM_ID = "item_id";
    public static final String NUMBER_ITEM = "number_item";
    
    public static final String INVENTORY = "inventory";
    
    public static final String DOG = "dog";
    public static final String WAKE_ITEM = "wake_item";
    public static final String NEXT_SLEEP = "next_sleep";
    
    public static final String QUEST_ID = "quest_id";
    public static final String QUEST = "quest";
    public static final String QUEST_STATE = "state";
    public static final String QUEST_INDEX = "quest_index";
    public static final String QUEST_COST = "quest_cost";
    public static final String MAIN_QUEST = "main_quest";
    public static final String MAIN_QUEST_INDEX = "index";
    public static final String MAIN_QUEST_STATE = "state";
    public static final String LAST_ACCEPT_LEVEL = "accept_level";
    public static final String QUEST_LINE = "quest_line";
    public static final String ACHIEVEMENTS = "achievements";
    public static final String ACHIEVEMENTS_INDEX = "index";
    public static final String ACHIEVEMENT_ID = "achievement_id";
    public static final String MEDAL = "medal";
    
    public static final String UIDATA = "ui_data";
    public static final String SETTINGS = "settings";
    
    public static final String GIFTS = "gifts";
    public static final String GIFT_ID = "gift_id";
    public static final String PRIZE = "prize";
    public static final String GIFT_PRIZE = "prize";
    public static final String PRIZE_ID = "prize_id";
    public static final String GIFT_EXPIRED_TIME = "expired_at";
    public static final String RECEIVERS = "receivers";
    public static final String GIFT_TEMPLATE_ID = "gift_template_id";
    public static final String REMAINS_GIFT = "n_remains_gift";
    public static final String NUMBER_GIFT_CODE = "n_code";
    public static final String GIFT_CODE_EXPIRED_TIME = "expire";
    public static final String GIFT_TEMPLATE_DATA = "data";
    public static final String GIFT_CODE_ID = "code";
    
    
    public static final String MAIL_ID = "mail_id";
    public static final String MAIL_SEND_TIME = "time";
    public static final String MAIL_SENDER = "sender";
    public static final String MAIL_CONTENT = "content";
    public static final String MAIL_OFFSET = "offset";
    public static final String MAIL_LENGTH = "mail_length";
    public static final String MAIL_DATA = "mail_data";
    public static final String NUMBER_NEW_MAILS = "nunread_mail";
    
    public static final String PENGUINDEX = "penguindex";
    
    public static final String EXP = "exp";
    public static final String LEVEL = "level"; 
    public static final String KIND = "kind";
    public static final String NAME = "name";
    public static final String FISH = "fish";
    public static final String GOLD = "gold";
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
    
    public static final String USER_TEMP_DATA = "temp_data";
    public static final String WAKE_DOG_FIRST_TIME = "wake_dog_first_time";
    public static final String LAST_TIME_SYNC_FRIEND_LIST = "last_sync_friends";
    
    public static final String USER_DAILY_DATA = "daily_data";
    public static final String TAKEN_SNAPSHOT = "taken_snapshot";
    public static final String RECEIVED_LOGIN_PRIZE = "received_login_prize";
    public static final String TAKEN_ADS = "taken_ads";
    
    public static final String ZING_COIN = "zing_coin";
    public static final String ZING_CRE_ID = "zing_cre_id";
    public static final String BILL_NO = "bill_no";
    
    public static final String WHITE_LIST = "whitelist";
}
