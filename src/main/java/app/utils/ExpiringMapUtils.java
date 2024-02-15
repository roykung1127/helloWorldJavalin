package app.utils;

import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ExpiringMapUtils {
    private static final Object lock = new Object();
    private static ExpiringMapUtils eMapUtils = null;
    private static ExpiringMap<String, String> eCache;

    private ExpiringMapUtils() {
        eCache = ExpiringMap.builder().variableExpiration().build();
    }

    public static ExpiringMapUtils getInstance() {
        synchronized (lock) {
            if (null == eMapUtils) {
                eMapUtils = new ExpiringMapUtils();
            }
        }
        return (eMapUtils);
    }

    public ExpiringMap<String, String> getCache() {
        return eCache;
    }



}
