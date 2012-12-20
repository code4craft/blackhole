package us.codecraft.blackhole.zones;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import us.codecraft.blackhole.utils.DoubleKeyMap;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class AnswerContainer implements AnswerProvider {

	private DoubleKeyMap<String, Integer, String> cache;

	public AnswerContainer() {
		cache = new DoubleKeyMap<String, Integer, String>(
				ConcurrentHashMap.class);
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

	public void add(String query, int type, String answer) {
		cache.put(query, type, answer);
	}

}
