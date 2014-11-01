/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services;

import java.util.NavigableMap;
import java.util.TreeMap;
import libCore.config.Config;
import pgentity.User;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class AutoMigrate {
    private AutoMigrate()
    {
        super();
        this.init();
    }
    
    private static final AutoMigrate inst = new AutoMigrate();
    
    public static AutoMigrate inst()
    {
        return inst;
    }
    
    private NavigableMap<Integer, Migrator> migrators = new TreeMap();
    
    public void migrate(String uid)
    {
        User user = User.getUser(uid);
        int currentVersion = user.getDbVer();
        int targetVersion = PGHelper.toInteger(Config.getParam("db_version", "db_ver"));
        
        while (currentVersion < targetVersion)
        {
            Integer nextVer = this.migrators.higherKey(currentVersion);
            if (nextVer == null)
            {
                break;
            }
            
            Migrator migrator = this.migrators.get(nextVer);
            migrator.migrate(uid);
            currentVersion = nextVer;
        }
        
        currentVersion = targetVersion;
        
        user.updateFromDB();
        user.setDbVer(targetVersion);
        user.saveToDB();
    }
    
    private void init()
    {
        
    }
    
    private static interface Migrator
    {
        void migrate(String uid);
    }
}