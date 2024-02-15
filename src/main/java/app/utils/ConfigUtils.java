package app.utils;

public class ConfigUtils {
	public static String getConfig(String name) {
		return getConfig(name, "");
	}

	public static String getConfig(String name, String defaultValue) {
		try {
			String value = Config.getInstance().getValue(name);
			Log.info("Getting app config for " + name + ", value = " + value);
			return value;
		} catch(Exception e) {
			Log.info("Getting app config for " + name + ", config not found , return defaultValue " + defaultValue);
			return defaultValue;
		}
	}
	
	public static int getConfigAsInt(String name) {
		return getConfigAsInt(name, -1);
	}
	
	public static int getConfigAsInt(String name, int defaultValue) {
		try {
			int value = Integer.valueOf(Config.getInstance().getInstance().getValue(name));
			Log.info("Getting app config for " + name + ", value = " + value);
			return value;
		} catch(Exception e) {
			Log.info("Getting app config for " + name + ", config not found , return defaultValue " + defaultValue);
			return defaultValue;
		}
	}
	
	public static long getConfigAsLong(String name) {
		return getConfigAsLong(name, -1L);
	}
	
	public static long getConfigAsLong(String name, long defaultValue) {
		try {
			long value = Long.valueOf(Config.getInstance().getInstance().getValue(name));
			Log.info("Getting app config for " + name + ", value = " + value);
			return value;
		} catch(Exception e) {
			Log.info("Getting app config for " + name + ", config not found , return defaultValue " + defaultValue);
			return defaultValue;
		}
	}
	
}

