/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import com.google.common.base.Function;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.reflect.FieldUtils;
import share.PGException;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class CFTemp implements JSONable
{
    private static final Map<Class<?>, Function<Object, Object>> CONVERTERS = new HashMap();
    
    static
    {
        CONVERTERS.put(int.class, new Function<Object, Object>() {
            @Override
            public Object apply(Object f) {
                return PGHelper.toInteger(f);
            }
        });
        CONVERTERS.put(float.class, new Function<Object, Object>() {
            @Override
            public Object apply(Object f) {
                return PGHelper.toFloat(f);
            }
        });
        CONVERTERS.put(long.class, new Function<Object, Object>() {
            @Override
            public Object apply(Object f) {
                return PGHelper.toLong(f);
            }
        });
        CONVERTERS.put(String.class, new Function<Object, Object>() {
            @Override
            public Object apply(Object f) {
                return String.valueOf(f);
            }
        });
        CONVERTERS.put(String[].class, new Function<Object, Object>() {
            @Override
            public Object apply(Object f) {
                String fStr = String.valueOf(f);
                return fStr.split(";");
            }
        });
    }
    
    private CFTemp() {}
    static CFTemp parse(Map<String, Object> json)
    {
        CFTemp temp = new CFTemp();
        temp.deser(json);
        
        return temp;
    }
    
    private float DigestTimePerFish;
    private float PenguinExpPerFish;
    private int PenguinDefaultLevel;
    private int BuyPenguinExp_Exp;
    private int BuyPenguinExp_Cost;
    private int BuyPenguinLevel_ExpPerCoin;
    private int MaxInventory;
    private int SessionTime;
    private int NumberDailyQuest;
    private int CotesEggs_Limit;
    private int CotesEggs_Limit_Display;
    private long MaxDogTime;
    private long WakeDogFirstTime_Time;
    private int TakeSnapshotFirstTimeInDay_Prize;
    private int AutoSyncFriends_Day;
    private long SyncFriendsCooldown;
    private int AutoRemoveNPC_Level;
    private int AutoRemoveNPC_Friends;
    private int RandomizePrize_Expire;
    private String Admin_UID;
    private String AdminTool_UID;
    private String SystemPasswordMD5;
    private int AdultPenguin_Level;
    private int MaxBuyRPByGoldPerDay;
    private int MaxBuyRPByCoinPerDay;

    @Override
    public void deser(Map<String, Object> json) {
        for (Map.Entry<String, Object> tmpEntry : json.entrySet()) {
            String fieldName = tmpEntry.getKey();
            Object value = tmpEntry.getValue();
            
            Field field = FieldUtils.getField(getClass(), fieldName, true);
            if (field != null)
            {
                Class<?> fldClass = field.getType();
                if (CONVERTERS.containsKey(fldClass))
                {
                    try {
                        Function<Object, Object> converter = CONVERTERS.get(fldClass);
                        field.set(this, converter.apply(value));
                    } catch (IllegalArgumentException ex) {
                        PGException.pgThrow(ex, "Invalid convert");
                    } catch (IllegalAccessException ex) {
                        PGException.pgThrow(ex, "Invalid convert");
                    }
                }
            }
        }
    }

    public float DigestTimePerFish() {
        return DigestTimePerFish;
    }

    public float PenguinExpPerFish() {
        return PenguinExpPerFish;
    }

    public int PenguinDefaultLevel() {
        return PenguinDefaultLevel;
    }

    public int BuyPenguinExp_Exp() {
        return BuyPenguinExp_Exp;
    }

    public int BuyPenguinExp_Cost() {
        return BuyPenguinExp_Cost;
    }

    public int MaxInventory() {
        return MaxInventory;
    }

    public int SessionTime() {
        return SessionTime;
    }

    public int NumberDailyQuest() {
        return NumberDailyQuest;
    }

    public int CotesEggs_Limit() {
        return CotesEggs_Limit;
    }

    public int CotesEggs_Limit_Display() {
        return CotesEggs_Limit_Display;
    }

    public long MaxDogTime() {
        return MaxDogTime;
    }

    public long WakeDogFirstTime_Time() {
        return WakeDogFirstTime_Time;
    }

    public int TakeSnapshotFirstTimeInDay_Prize() {
        return TakeSnapshotFirstTimeInDay_Prize;
    }

    public int AutoSyncFriends_Day() {
        return AutoSyncFriends_Day;
    }

    public long SyncFriendsCooldown() {
        return SyncFriendsCooldown;
    }

    public int AutoRemoveNPC_Level() {
        return AutoRemoveNPC_Level;
    }

    public int AutoRemoveNPC_Friends() {
        return AutoRemoveNPC_Friends;
    }

    public int RandomizePrize_Expire() {
        return RandomizePrize_Expire;
    }

    public String Admin_UID() {
        return Admin_UID;
    }

    public String AdminTool_UID() {
        return AdminTool_UID;
    }

    public String SystemPasswordMD5() {
        return SystemPasswordMD5;
    }
    
    public int MaxBuyRPByGoldPerDay()
    {
        return MaxBuyRPByGoldPerDay;
    }
    
    public int MaxBuyRPByCoinPerDay()
    {
        return MaxBuyRPByCoinPerDay;
    }
    
    public int AdultPenguin_Level()
    {
        return AdultPenguin_Level;
    }
    
    public int BuyPenguinLevel_ExpPerCoin()
    {
        return this.BuyPenguinLevel_ExpPerCoin;
    }
}