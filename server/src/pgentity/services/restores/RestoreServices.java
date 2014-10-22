/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services.restores;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import libCore.config.Config;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import share.AMFBuilder;
import share.PGError;
import share.PGException;
import share.PGHelper;
import share.PGMacro;
import share.TimeUtil;

/**
 *
 * @author KieuAnh
 */
public class RestoreServices {
    private RestoreServices()
    {
        super();
        this.init();
    }
    
    private static final RestoreServices inst = new RestoreServices();
    
    public static RestoreServices inst()
    {
        return inst;
    }
    
    private static final String BACKUP_DIR = Config.getParam("a1backup", "directory");
    private NavigableMap<Integer, Restorer> restorers = new TreeMap();
    
    private File getFile(String uid, Date day)
    {
        String dayToken = TimeUtil.dayToken(day.getTime());
        File dayDir = new File(BACKUP_DIR + dayToken);
        return new File(dayDir.getAbsolutePath() + '/' + uid + ".bak"); 
    }
    
    private List<File> getA1File(Date day)
    {
        String dayToken = TimeUtil.dayToken(day.getTime());
        File dayDir = new File(BACKUP_DIR + dayToken);
        if (dayDir.exists() && dayDir.isDirectory())
        {
            return Arrays.asList(dayDir.listFiles());
        }
        
        return Collections.EMPTY_LIST;
    }
    
    public void restoreUser(String uid, Date day)
    {
        File file = getFile(uid, day);
        PGException.Assert(file.exists(),
                PGError.UNDEFINED, "Backup file: " + file + " doesn't existed");
        
        try {
            restoreFromFile(uid, file);
        } catch (IOException ex) {
            PGException.pgThrow(ex, "Cannot read file" + file.getAbsolutePath());
        }
    }
    
    private int MAX_DAY_OF_SEARCH = 30;
    public Map<String, Date> restoreUser(String uid)
    {
        Date toDay = new Date();
        File file = null;
        Date day = toDay;
        for (int i = 0; i <= MAX_DAY_OF_SEARCH; ++i)
        {
            day = DateUtils.addDays(toDay, -i);
            file = getFile(uid, day);
            if (file.exists())
            {
                break;
            }
            
            file = null;
        }
        
        PGException.Assert(file != null && file.exists(),
                PGError.UNDEFINED,
                "Backup for " + uid + " doesn't existed in last 30 days");
        
        try {
            restoreFromFile(uid, file);
        } catch (IOException ex) {
            PGException.pgThrow(ex, "Cannot read file" + file.getAbsolutePath());
        }
        
        return (Map) AMFBuilder.make(uid, day);
    }
    
    public Map<String, Date> restoreA1(Date day)
    {
        List<File> files = getA1File(day);
        Map<String, Date> ret = new HashMap(files.size());
        
        for (File file : files) {
            try {
                final String uid = FilenameUtils.removeExtension(file.getName());
                restoreFromFile(uid, file);
                ret.put(uid, day);
            } catch (IOException ex) {
                Logger.getLogger(RestoreServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return ret;
    }
    
    public Map<String, Date> restoreA7(Date lastDay)
    {
        return restoreIn(DateUtils.addDays(lastDay, -6), lastDay);
    }
    
    public Map<String, Date> restoreA30(Date lastDay)
    {
        return restoreIn(DateUtils.addDays(lastDay, -29), lastDay);
    }
    
    public Map<String, Date> restoreIn(Date from, Date to)
    {
        PGException.Assert(from.before(to), PGError.UNDEFINED,
                "From day " + from + " must be lesser than to day " + to);
        Map<String, Pair<Date, File>> lastActive = new HashMap();
        for (Date day = from; day.before(to) || DateUtils.isSameDay(day, to);
                day = DateUtils.addDays(day, 1))
        {
            List<File> files = getA1File(day);
            for (File file : files) {
                lastActive.put(FilenameUtils.removeExtension(file.getName()),
                        Pair.of(day, file));
            }
        }
        
        Map<String, Date> ret = new HashMap(lastActive.size());
        for (Map.Entry<String, Pair<Date, File>> restEntry : lastActive.entrySet()) {
            String uid = restEntry.getKey();
            File file = restEntry.getValue().getValue();
            
            try {
                restoreFromFile(uid, file);
                ret.put(uid, restEntry.getValue().getKey());
            } catch (IOException ex) {
                Logger.getLogger(RestoreServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return ret;
    }

    private void init() {
        restorers.put(0, V0_Restorer.inst());
    }
    
    private void restoreFromFile(String uid, File file)
            throws IOException
    {
        String text = new String(
                Files.readAllBytes(Paths.get(file.toURI())),
                StandardCharsets.UTF_8);

        JSONObject data = (JSONObject) JSONValue.parse(text);
        int dbVer = PGHelper.toInteger(data.get(PGMacro.DB_VERSION));
        Restorer restorer = restorers.get(restorers.floorKey(dbVer));
        restorer.restore(uid,
                (JSONObject) data.get("data"),
                System.currentTimeMillis());
    }
}
