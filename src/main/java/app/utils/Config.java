package app.utils;

import io.javalin.http.Context;
import org.apache.commons.lang3.StringUtils;
import java.util.ResourceBundle;

import static app.main.MainApp.ENV;

public class Config {

    public static final int maxAge = 3600 * 24 * 60;
    public static final String OTTSESSIONID = "OTTSESSIONID";
    public static final String NMAF_uuid = "NMAF_uuid";
    public static final String WEB_DEVICE = "web";
    public static final Object lock = new Object();
    public static Config config = null;
    public static ResourceBundle rb = null;


    public Config() {
        rb = ResourceBundle.getBundle("properties/config-"+ENV);
    }

    public static Config getInstance() {
        synchronized (lock) {
            if (null == config) {
                config = new Config();
            }
        }
        return (config);
    }

    public String getValue(String key) {
        return rb.getString(key);
    }

    public static String getDeviceUUID(Context ctx) {
        String uuid = SessionCookies.get(ctx, NMAF_uuid);
        if (StringUtils.isBlank(uuid)) {
            String ts = Long.toHexString(System.currentTimeMillis() / 1000);
            String s4 = Long.toHexString(Math.round((1 + Math.random()) * 0x10000)).substring(1);
            uuid = "w-" + ts + "-" + s4 + "-" + s4 + "-" + s4 + "-" + s4 + s4;
            SessionCookies.put(ctx,"NMAF_uuid", uuid);
        }
        return uuid;
    }
}
