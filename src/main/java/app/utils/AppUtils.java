package app.utils;

import kong.unirest.Unirest;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AppUtils {
	public static final Object lock = new Object();
	public static AppUtils appUtils = null;
	public static ResourceBundle rb = null;
	private static Map<String, String> appConfigCache;

	public AppUtils() {
		rb = ResourceBundle.getBundle("settings/settings");
		appConfigCache = ExpiringMap.builder().expiration(5, TimeUnit.MINUTES).build();
	}

	public static AppUtils getInstance() {
		synchronized (lock) {
			if (null == appUtils) {
				appUtils = new AppUtils();
			}
		}
		return (appUtils);
	}

	public String getSettingValue(String key) {
		return rb.getString(key);
	}

	public String getAppConfigCache(String key) {
		return appConfigCache.get(key);
	}

	public void setAppConfigCache(String key, String value) {
		appConfigCache.put(key, value);
	}

	public static void setCachedOverflowData(String value) {
		AppUtils.getInstance().setAppConfigCache("OverflowData", value);
	}

	public static boolean isTrueStr(String value) {
        return "Y".equalsIgnoreCase(value)||"true".equalsIgnoreCase(value);
    }
	
	public static String getCachedOverflowData() {
		String oD = AppUtils.getInstance().getAppConfigCache("OverflowData");
		if (StringUtils.isBlank(oD)) {
			oD = FlatFile.getData();
			AppUtils.getInstance().setAppConfigCache("OverflowData", oD );
		}
		return oD;
	}

}
