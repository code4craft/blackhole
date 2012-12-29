package us.codecraft.blackhole.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import us.codecraft.blackhole.config.Configure;
import us.codecraft.wifesays.me.StandReadyWorker;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 19, 2012
 */
@Component
public class EhcacheClient extends StandReadyWorker implements CacheClient,
		InitializingBean {

	private Logger logger = Logger.getLogger(EhcacheClient.class);

	@Autowired
	private Configure configure;

	private CacheManager manager;

	private static final String CACHE_NAME = "DNS";
	private static final String CACHE_CONF = "ehcache.xml";

	private static final String CLEAR = "clear_cache";

	/**
	 * (non-Jsdoc)
	 * 
	 * @see com.CacheClient.mail.postman.cache.service.impl.CacheClient#init()
	 */
	@Override
	public void init() {
		ClassPathResource classPathResource = new ClassPathResource(CACHE_CONF);
		InputStream inputStream = null;
		try {
			inputStream = classPathResource.getInputStream();
			manager = new CacheManager(inputStream);
		} catch (Exception e) {
			logger.error("init ehcache error!", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				logger.warn("close error", e);
			}
			inputStream = null;
		}
	}

	/**
	 * (non-Jsdoc)
	 * 
	 * @see com.dianping.mail.postman.cache.service.CacheService#get(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		if (manager == null || key == null) {
			return null;
		}
		if (manager == null) {
			return null;
		}
		Cache cache = manager.getCache(CACHE_NAME);
		Element element = cache.get(key);
		if (element == null) {
			return null;
		}
		T value = (T) element.getObjectValue();
		return value;
	}

	/**
	 * (non-Jsdoc)
	 * 
	 * @see com.dianping.mail.postman.cache.service.CacheService#set(java.lang.String,
	 *      java.lang.Object, int)
	 */
	@Override
	public <T> boolean set(String key, T value, int expireTime) {
		if (!configure.isUseCache()) {
			return false;
		}
		if (key == null || value == null) {
			throw new IllegalArgumentException(
					"key and value should not be null");
		}
		if (manager == null) {
			return false;
		}
		Cache cache = manager.getCache(CACHE_NAME);
		Element element = new Element(key, value, Boolean.FALSE, 0, expireTime);
		cache.put(element);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.wifesays.me.StandReady#doWhatYouShouldDo(java.lang.String)
	 */
	@Override
	public String doWhatYouShouldDo(String whatWifeSays) {
		if (CLEAR.equalsIgnoreCase(whatWifeSays)) {
			if (manager == null) {
				return "CACHE NOT USED";
			}
			clearCache();
			return "REMOVE SUCCESS";
		}
		return null;
	}

	public void clearCache() {
		Cache cache = manager.getCache(CACHE_NAME);
		@SuppressWarnings("unchecked")
		List<String> keys = cache.getKeys();
		logger.info(keys.size() + " cached records cleared");
		if (logger.isDebugEnabled()) {
			logger.debug("[" + StringUtils.join(keys, ",") + "]");
		}
		cache.removeAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (configure.isUseCache()) {
			new Thread() {
				@Override
				public void run() {
					init();
				}
			}.start();
		}
	}

}
