/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.util.UUID;
import libCore.config.Config;
import org.apache.commons.lang.RandomStringUtils;
import share.PGMacro;

/**
 * Define some default Redis root keys for Penguins
 * Use for make Redis key
 * @author LinhTA
 */
public class PGKeys
{
    public static final RedisKey USERS = RedisKey.root().getHashChild("u");
    public static final RedisKey USER_LIST = RedisKey.root().getChild("user_list");
    public static final RedisKey ALL_USERS = USERS;
    public static final RedisKey CONFIGS = RedisKey.root().getChild("config");
    public static final RedisKey INC = RedisKey.root().getChild("inc");
    
    public static final RedisKey GIFTS = RedisKey.root().getChild("gifts");
    public static final RedisKey PAYMENTS = RedisKey.root().getChild("payments");
    public static final RedisKey GIFTCODES = RedisKey.root().getChild("giftcodes");
    public static final RedisKey GAMEMESSAGES = RedisKey.root().getChild("msg");
    public static final RedisKey GIFTTEMPLATES = RedisKey.root().getChild("gifttemplates");
    public static final RedisKey RACING = RedisKey.root().getChild("race");
    
    public static final String FD_COTES = "cotes";
    public static final String FD_FRIENDSLIST = "friends";
    public static final String FD_BOXEGG = "boxegg";
    public static final String FD_PENGUINS = "penguins";
    public static final String FD_EGGS = PGMacro.EGGS;
    public static final String FD_INVENTORY = "inventory";
    public static final String FD_MAILS = "mails";
    public static final String FD_PLAY_WITH_FRIEND = "playwithfriend";
    public static final String FD_STEAL_EGG = "stealegg";
    public static final String FD_HELP_FISH = "helpfish";
    public static final String FD_DOG = "dog";
    public static final String FD_UIDATA = "ui";
    public static final String FD_SETTINGS = "settings";
    public static final String FD_GIFTS = "gifts";
    public static final String FD_ACHIVEMENTS = "achivements";
    public static final String FD_PENGUINDEX = "penguindex";
    public static final String FD_NPC_LIST = "npcs";
    public static final String FD_TEMP_DATA = "temp";
    public static final String FD_DAILY_DATA = "dailydata";
    
    public static final String FD_QUEST = "quest";
    public static final String FD_DAILY_QUEST = "daily";
    public static final String FD_MAIN_QUEST = "main";
    public static final String FD_QUEST_LOGGER = "logger";
    
    public static final String FD_RANK = "rank";
    
    public static final String FD_NOTIF = "notif";

    private static final String DB_VER_PREFIX = Config.getParam("db_version", "db_ver") + "_";
    private static final long PACK_SIZE = 0x1000;
    private static volatile long currentKey = Long.MAX_VALUE;
    private static long currentPack = 0;
    private static long nextPack = 0;
    public static synchronized String randomKey() {
        if (currentKey >= nextPack)
        {
            currentPack = DBContext.Redis().incrBy(INC, PACK_SIZE);
            nextPack = currentPack + PACK_SIZE;
            currentKey = currentPack;
        }
        return DB_VER_PREFIX +
                Long.toString(currentKey++, Character.MAX_RADIX);
    }

    private static String randomKey2() {
        UUID rand = UUID.randomUUID();
        return Long.toHexString(rand.getMostSignificantBits() + 37 * 
                rand.getLeastSignificantBits());
    }
    
    private static String randomKey3()
    {
        return UUID.randomUUID().toString();
    }
    
    private static final int N_CODE_CHAR = 6;
    public static String randomCode()
    {
        return RandomStringUtils.randomAlphanumeric(N_CODE_CHAR);
    }
    public static String randomCode(int nCodeChar)
    {
        return RandomStringUtils.randomAlphanumeric(nCodeChar);
    }
}