package us.codecraft.blackhole.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import us.codecraft.blackhole.config.Configure;
import us.codecraft.blackhole.utils.RecordUtils;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 19, 2012
 */
@Component
public class CacheManager implements InitializingBean {

	@Autowired
	private Configure configure;

	private volatile ExecutorService cacheSaveExecutors;

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private CacheClient cacheClient;

	public byte[] getFromCache(Message query) {
		if (!configure.isUseCache()) {
			return null;
		}
		Record question = query.getQuestion();
		UDPPackage udpPackage = cacheClient
				.<UDPPackage> get(getCacheKey(question));
		if (udpPackage == null) {
			return null;
		}
		byte[] bytes = udpPackage.getBytes(query.getHeader().getID());
		return bytes;
	}

	private String getCacheKey(Record question) {
		return RecordUtils.recordKey(question);
	}

	private int minTTL(Message response) {
		return (int) Math.min(RecordUtils.maxTTL(response
				.getSectionArray(Section.ANSWER)), RecordUtils.maxTTL(response
				.getSectionArray(Section.ADDITIONAL)));
	}

	public void setToCache(final Message query, final byte[] responseBytes) {
		if (configure.isUseCache()) {
			getCacheSaveExecutors().execute(new Runnable() {

				@Override
				public void run() {
					try {
						Message response = new Message(responseBytes);
						Record question = query.getQuestion();
						cacheClient.set(getCacheKey(question), new UDPPackage(
								responseBytes), minTTL(response));
					} catch (Throwable e) {
						logger.warn("set to cache error " + e);
					}

				}
			});
		}

	}

	/**
	 * @return the cacheSaveExecutors
	 */
	public ExecutorService getCacheSaveExecutors() {
		if (cacheSaveExecutors == null) {
			synchronized (this) {
				cacheSaveExecutors = Executors.newFixedThreadPool(50);
			}
		}
		return cacheSaveExecutors;
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
			getCacheSaveExecutors();
		}
	}

}
