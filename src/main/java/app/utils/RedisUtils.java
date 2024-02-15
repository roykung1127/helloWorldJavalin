package app.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {

	private static volatile JedisPool instance;


	public static JedisPool getInstance() {
		if (instance == null) {
			synchronized (JedisPool.class) {
				if (instance == null) {
					Log.log("Create Redis instance...", LogType.INFO);
					JedisPoolConfig poolConfig = new JedisPoolConfig();
					poolConfig.setMaxTotal(128);
					poolConfig.setMaxIdle(128);
					poolConfig.setMinIdle(16);
					instance = new JedisPool(poolConfig, Config.getInstance().getValue("redis-endpoint"), Integer.getInteger(Config.getInstance().getValue("redis-port"), 6379));
				}
			}
		}

		return instance;
	}

	public static Jedis getJedisResources() {
		JedisPool jedisPool = getInstance();
		Jedis jedis = jedisPool.getResource();
		return jedis;
	}


	public static String getKeys(String key, String prefix, String version) {
		Log.log("Get Redis Key " + key + " prefix " + prefix, LogType.INFO);
		String value = null;
		int retryCount = 0;

		do {
			String redisKey = null;
			if (retryCount > 6) {
				break;
			}
			if (version != null) {
				redisKey = prefix + "_" + key + "_" + version;
			} else {
				redisKey = prefix + "_" + key;
			}
			Jedis jedis = null;
			try {
				jedis = getJedisResources();
				value = jedis.get(redisKey);
				if (value == null || "".equals(value)) {
					++retryCount;
					continue;
				}
			} catch (Exception jex) {
				Log.log("Get Redis Key Error:" + redisKey + " ", LogType.ERROR, jex);
				++retryCount;
				continue;
			} finally {
				if (null != jedis) {
					jedis.close();
				}
			}
			break;
		} while (true);
		return value;
	}

	public static boolean setKeys(String key, String value, String prefix, String version, int expire) {
		boolean result = false;
		do {
			Jedis jedis = null;
			try {
				jedis = getJedisResources();
				String redisKey = null;
				if (version != null) {
					redisKey = prefix + "_" + key + "_" + version;
				} else {
					redisKey = prefix + "_" + key;
				}
				Log.log("Redis keys: " + redisKey + " | value : " + value + " | expire: " + expire, LogType.INFO);
				String setKeyResponse = jedis.set(redisKey, value);
				if (!"OK".equals(setKeyResponse)) {
					Log.log("Fail to set KeyResponse in Redis for the key: " + redisKey, LogType.ERROR);
					break;
				}

				if (expire > -1) {
					long setExpireResponse = jedis.expire(redisKey, (long) expire);
					Log.log("Redis Expire response : " + setExpireResponse, LogType.INFO);
					if (setExpireResponse != 1) {
						Log.log("Fail to set ExpireResponse in Redis for the key: " + redisKey, LogType.ERROR);
						break;
					}
				}

			} catch (Exception jex) {
				Log.log("Exception is found in setRedis. Message: " + jex.getMessage(), LogType.ERROR);
			} finally {
				if (null != jedis) {
					jedis.close();
				}
			}
			result = true;
		} while (false);
		return result;
	}

	public static boolean removeRedis(String key, String prefix, String version) {
		boolean result = false;
		do {
			Jedis jedis = null;
			try {
				jedis = getJedisResources();
				String redisKey = null;
				if (version != null) {
					redisKey = prefix + "_" + key + "_" + version;
				} else {
					redisKey = prefix + "_" + key;
				}
				Long numberOfKeyRemoved = jedis.del(redisKey);
				Log.log("numberOfKeyRemoved: " + numberOfKeyRemoved, LogType.INFO);
			} catch (Exception jex) {
				Log.log("Exception is found in removeRedis. Message: " + jex.getMessage(), LogType.ERROR);
			} finally {
				if (null != jedis) {
					jedis.close();
				}
			}
			result = true;
		} while (false);
		return result;
	}

	public static boolean updateCounter(String key, int expire) {
		boolean result = false;
		do {
			Jedis jedis = null;
			String redisKey = "Counter_" + key;
			try {
				jedis = getJedisResources();
				Long setKeyResponse = jedis.incr(redisKey);
				//	Log.log("Redis:"+redisKey+" Count:"+setKeyResponse, logType.INFO);
				long setExpireResponse = jedis.expire(redisKey, (long) expire);
			} catch (Exception jex) {
				Log.log("Exception is found in updateCounter. Message: " + jex.getMessage(), LogType.ERROR);
			} finally {
				if (null != jedis) {
					jedis.close();
				}
			}
			result = true;
		} while (false);
		return result;
	}

	public static long getCounter(String key) {
		String value = null;
		int retryCount = 0;
		long counter = 0;
		String redisKey = "Counter_" + key;
		do {
			if (retryCount > 3) {
				break;
			}
			Jedis jedis = null;
			try {
				jedis = getJedisResources();
				value = jedis.get(redisKey);
				if (value == null || "".equals(value)) {
					++retryCount;
					continue;
				}
				counter = Long.parseLong(value);
			} catch (Exception jex) {
				Log.log("Get Redis Key Error:" + redisKey + " ", LogType.ERROR, jex);
				++retryCount;
				continue;
			} finally {
				if (null != jedis) {
					jedis.close();
				}
			}
			break;
		} while (true);
		return counter;
	}
	public static void close() {
		Log.info("redis close");
		if (null != instance) {
			instance.close();
		}
	}
}
