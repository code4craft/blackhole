package us.codecraft.blackhole.zones;

import java.util.concurrent.ConcurrentHashMap;

import us.codecraft.blackhole.DoubleKeyMap;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
public class AnswerCacheContainer implements AnswerProvider {

	private DoubleKeyMap<String, Integer, String> cache;

	private AnswerCacheContainer() {
		cache = new DoubleKeyMap<String, Integer, String>(
				ConcurrentHashMap.class);
	}

	private static final AnswerCacheContainer INSTANCE = new AnswerCacheContainer();

	public static AnswerCacheContainer instance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.blackhole.zones.AnswerProvider#getAnswer(java.lang.String,
	 * int)
	 */
	@Override
	public String getAnswer(String query, int type) {
		return cache.get(query, type);
	}

	public void addCache(String query, int type, String answer) {
		cache.put(query, type, answer);
	}

}
