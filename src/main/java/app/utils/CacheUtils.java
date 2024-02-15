package app.utils;

import net.jodah.expiringmap.ExpirationPolicy;

import java.util.concurrent.TimeUnit;

public class CacheUtils {

    private static final String ver = "1";
    private static final String prefix = "cdns";

    public static void setData (String key, String value, int expiry){
        if (redisCacheMode()) {
            RedisUtils.setKeys(key, value, prefix, ver, expiry);
        } else {
            //Dev
            ExpiringMapUtils.getInstance().getCache().put(key, value, expiry, TimeUnit.SECONDS);
        }
    }

//    public static void setData (String key, String value){
//        Log.info("CacheUtil setData key:" + key + " value:" + value);
//        if (redisCacheMode()) {
//            RedisUtils.setKeys(key, value, prefix, ver, -1);
//        } else {
//            //Dev
//            ExpiringMapUtils.getInstance().getCache().put(key, value);
//        }
//    }

    public static String getData (String key) {
        String value = "";
        if (redisCacheMode()) {
            value = RedisUtils.getKeys(key, prefix, ver);
        } else {
            value =  ExpiringMapUtils.getInstance().getCache().get(key);
        }
        Log.info("CacheUtil getData key:" + key + " value:" + value);
        return value;
    }


    public static void removeData (String key) {
        Log.info("CacheUtil removeData key:" + key );
        if (redisCacheMode()) {
            RedisUtils.removeRedis(key, prefix, ver);
        } else {
            ExpiringMapUtils.getInstance().getCache().remove(key);
        }
    }


    private static Boolean redisCacheMode() {
        if (Config.getInstance().getValue("atvcache.mode").equalsIgnoreCase("redis")) {
            return true;
        } else {
            return false;
        }
    }

}
