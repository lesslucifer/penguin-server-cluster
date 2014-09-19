/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share;

import libCore.config.Config;
import share.utilities.TimeUtil;

/**
 * Define some hard constants
 * Almost of them are ad-hoc - should be changed later
 * @author KieuAnh
 */
public class PGConst
{
    public static final float FISH_TO_TIME = 10f;
    public static final float FISH_TO_EXP = 1f;
    
    public static final int DEFAULT_PENGUIN_LEVEL_BUY = 0;
    public static final int MAX_EXP_PENGUIN_BUY = 30;
    public static final int BUY_PENGUIN_LEVEL_COST = 100;
    public static final float EXP_TO_COIN = 1f;
    
    public static final int MAX_EGG_IN_COTE = 30;
    public static final int MAX_INVENTORY = 1000;
    
    public static final int SESSION_TIME = 5*60;
    
    public static final int N_DAILY_QUEST = 5;
    
    public static final int LIMIT_DISPLAY_EGGS = 100;
    public static final int LIMIT_EGGS = 100;
    
    public static final int RANDOMIZE_RPIZE_COST = 2;
    
    public static final long MAX_DOG_TIME = 90L*24L*60L*60L*1000L;   // 90 days
    public static final long WAKE_DOG_FIRST_TIME_TIME = 7*24L*60L*60L*1000L; // 7 days
    
    public static final int DAY_SECS = 24*60*60;
    public static final int FIRST_TIME_TAKE_SNAPSHOT_IN_DAY_PRIZE = 3;
    public static final int NDAYS_RESYNC_FRIENDS = 1;
    public static final long TIME_COOLDOWN_SYNC_FRIENDS = 60L*60L*1000L;    // 1 hours
    public static final int REMOVE_NPC_LEVEL = 10;
    public static final int REMOVE_NPC_N_FRIENDS = 15;
    public static final int RP_PRIZE_EXPIRE = 7 * TimeUtil.DAY_SECS;
    
    public static final String ADMIN_UID = "288377918";
    public static final String ADMIN_TOOL_UID = "canhcutvuive";
    public static final String ADMIN_TOOL_PASSWORD = "1@3$5^bayTAM";
    
    public static final String A1BACKUP_DIR = Config.getParam("a1backup", "directory");
}