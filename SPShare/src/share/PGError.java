/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share;

/**
 *
 * @author KieuAnh
 */
public class PGError
{
    private PGError() {}
    
    public static final int UNDEFINED = -1;
    
    public static final int INVALID_CONFIG = -2;
    public static final int INVALID_USER = -3;
    public static final int INVALID_COTE = -4;
    public static final int NOT_USER_COTE = -5;
    public static final int NOT_FRIEND_COTE = -6;
    public static final int INVALID_SIGNED_REQUEST = -7;
    public static final int INVALID_SESSION = -8;
    
    public static final int NOT_ENOUGH_LEVEL = -101;
    public static final int NOT_ENOUGH_GOLD = -102;
    public static final int NOT_ENOUGH_COIN = -103;
    public static final int NOT_ENOUGH_FISH = -104;
    public static final int INCREASE_NEGATIVE_GOLD = -105;
    public static final int DECREASE_NEGATIVE_GOLD = -106;
    public static final int INCREASE_NEGATIVE_COIN = -107;
    
    public static final int NOT_ENGOUH_PENGUIN_SLOT = -201;
    public static final int EMPTY_POOL = -202;
    public static final int TOO_MUCH_FISH = -203;
    public static final int OUT_OF_COTE_LEVEL = -204;
    public static final int NULL_COTE_NAME = -205;
    public static final int FULL_COTE = -206;
    public static final int DOG_MAX_AWAKE_TIME = -207;
    
    public static final int OUT_OF_BOXEGG_LEVEL = -301;
    
    public static final int INVALID_PENGUIN = -401;
    public static final int PENGUIN_NOT_IN_COTE = -402;
    public static final int PENGUIN_NOT_HUNGRY = -403;
    public static final int PENGUIN_NOT_SPAWN = -404;
    public static final int PENGUIN_NOT_ENOUGH_LEVEL_SPAWN = -405;
    public static final int PENGUIN_CANNOT_SELL = -406;
    public static final int PENGUIN_CANNOT_EAT = -407;
    public static final int MAX_LEVEL_PENGUIN = -408;
    
    public static final int STEAL_EGG_WRONG = -501;
    public static final int INVALID_EGG = -502;
    public static final int NOT_ENOUGH_EGG = -503;
    
    public static final int INVALID_ITEM = -601;
    public static final int ITEM_NOT_PENGUIN = -602;
    public static final int ITEM_NOT_FISH = -603;
    public static final int INVALID_GIFT = -604;
    public static final int ITEM_NOT_GOLD = -605;
    public static final int INVALID_GIFT_CODE = -606;
    public static final int EXISTED_GIFT_CODE = -607;
    public static final int INVALID_GIFT_DATA = -608;
    
    public static final int INVALID_FRIEND = -701;
    public static final int FRIEND_NOT_FRIEND_WITH_USER = -702;
    public static final int USER_NOT_FRIEND_WITH_FRIEND = -703;
    public static final int OVER_STOLEN_EGG_TODAY = -704;
    public static final int OVER_FRIEND_STEAL_EGG_TODAY = -705;
    public static final int HAVE_HELPED_FRIEND_TODAY = -706;
    public static final int OVER_FRIEND_HELPED_TODAY = -707;
    public static final int TOO_MUCH_FRIEND_FISH = -708;
    
    public static final int NOT_ENOUGH_INVENTORY_SLOT = -801;
    
    public static final int INCOMPLETED_QUEST = -901;
    public static final int NOT_ENOUGH_RESOURCE_FOR_ACCEPT_QUEST = -902;
    public static final int INCOMPLETED_BEFORE_QUEST = -903;
    public static final int ACCEPT_NOT_NEW_QUEST = -904;
    public static final int QUEST_WAS_NOT_ACCEPTED = -905;
    public static final int QUEST_IS_UNLOCKED = -906;
    public static final int INVALID_QUEST_LINE = -907;
    public static final int CANNOT_PRIZE = -908;
    public static final int INVALID_ACHIVEMENT = -909;
    public static final int RECEIVED_ACHIEVEMENT = -910;
    public static final int ACHIEVEMENT_DISABLED = -911;
    public static final int NOT_ENOUGH_RP_TURN = -912;
    public static final int CANNOT_BUY_RP_TURN = -913;
    
    public static final int HAVE_WAKEN_DOG_FIRST_TIME = -1001;
    public static final int RECEIVED_LOGIN_PRIZE = -1002;
    public static final int HAVE_TAKEN_ADS_TODAY = -1003;
    public static final int SYNC_FRIEND_ARE_LOCKED = -1004;
    
    public static final int BILL_NO_ALREADY_EXISTED = -1101;
    public static final int BILL_NO_NOT_EXISTED = -1102;
    public static final int BILL_NO_NOT_BELONG_TO_USER = -1103;
    
    public static final int INVALID_MAIL = -1201;
    
    public static final int RETURNED_EVENT = -1301;
    public static final int INCOMPLETED_EVENT = -1302;
    
    public static final int INVALID_RACE = -1401;
    public static final int OVERLAPPED_RACE = -1402;
}
