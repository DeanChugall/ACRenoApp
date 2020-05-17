package rs.acreno.system.util.properties;

import org.apache.log4j.Logger;
import rs.acreno.autoservis.AutoServisController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class AcrenoProperties {

    private static final Logger logger = Logger.getLogger(AcrenoProperties.class);

    private final Properties configProp = new Properties();

    private AcrenoProperties() {
        //Private constructor to restrict new instances
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("acreno.properties");
        try {
            configProp.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Bill Pugh Solution for singleton pattern
    private static class LazyHolder {
        private static final AcrenoProperties INSTANCE = new AcrenoProperties();
    }

    public static AcrenoProperties getInstance() {
        return AcrenoProperties.LazyHolder.INSTANCE;
    }

    public String getProperty(String key) {
        return configProp.getProperty(key);
    }

    public Set<String> getAllPropertyNames() {
        return configProp.stringPropertyNames();
    }

    public boolean containsKey(String key) {
        return configProp.containsKey(key);
    }

    /**
     * ApplicationProperties cache = ApplicationProperties.getInstance();
     * if(cache.containsKey("country") == false){
     * cache.setProperty("country", "INDIA");
     * }
     * //Verify property
     * System.out.println(cache.getProperty("country"));
     *
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {
        configProp.setProperty(key, value);
    }
}


