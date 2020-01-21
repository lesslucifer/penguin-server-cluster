/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libCore.config;

import com.google.gson.Gson;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.log4j.Logger;

/**
 *
 * @author linhta
 */
public class Config
{
    private static final GuavaLoadingCache<CacheByteData> localconfig;
    private static final Gson jsonSerializer;

    public static final String CONFIG_HOME = "conf";
    public static final String CONFIG_FILE = "config.ini";
    private static Logger logger_ = Logger.getLogger(Config.class);
    static final CompositeConfiguration config;

    public static String getHomePath() {
        return System.getProperty("apppath");
    }

    public static String getParam(String section, String name) {
        UtilsCache<String, String> uCache = new UtilsCache<String, String>();
        String key = section + "." + name;

        String value = uCache.get(localconfig, jsonSerializer, key, String.class);

        if (value != null) {
            return value;
        }

        value = uCache.put(config, localconfig, jsonSerializer, key, String.class);
        return value;
    }

    static {
        jsonSerializer = new Gson();
        localconfig = new GuavaLoadingCache<CacheByteData>();

        String CONFIG_ITEMS = System.getProperty("cfg_items");
        String HOME_PATH = System.getProperty("apppath");
        String APP_ENV = System.getProperty("appenv");

        if ((CONFIG_ITEMS == null) || (CONFIG_ITEMS.equals(""))) {
            CONFIG_ITEMS = "500";
        }
        if (APP_ENV == null) {
            APP_ENV = "";
        }
        if (!"".equals(APP_ENV)) {
            APP_ENV = APP_ENV + ".";
        }

        localconfig.initCache(10000, 4, CacheByteData.class);

        config = new CompositeConfiguration();

        File configFile = new File(HOME_PATH + File.separator + "conf" + File.separator + APP_ENV + "config.ini");
        try {
            config.addConfiguration(new HierarchicalINIConfiguration(configFile));
            
            Iterator ii = config.getKeys();
            while (ii.hasNext()) {
                String key = (String) ii.next();
                localconfig.set(key.getBytes(), CacheByteData.wrap(config.getString(key).getBytes()));
            }
        } catch (Exception e) {
            System.out.println("Exception when Config");
            System.exit(1);
        }
    }
}
