package us.codecraft.blackhole.cache;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 19, 2012
 */
public interface CacheClient {

	/**
	 * If "cache" configure is turned off, it can get the elemets already in
	 * cache.
	 * 
	 * @param key
	 * @param value
	 * @param expireTime
	 *            in s
	 * @return
	 */
	<T> boolean set(String key, T value, int expireTime);

	/**
	 * If "cache" configure is turned off, it can't be setted and return false.
	 * 
	 * @param key
	 * @return
	 */
	<T> T get(String key);

	/**
	 * init cache
	 * 
	 * @throws Exception
	 */
	void init() throws Exception;

    void clearCache();

}
