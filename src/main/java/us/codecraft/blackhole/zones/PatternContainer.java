package us.codecraft.blackhole.zones;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xbill.DNS.Address;
import org.xbill.DNS.Type;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
public class PatternContainer implements AnswerProvider {

	private Map<Pattern, String> patterns;

	private Logger logger = Logger.getLogger(getClass());

	private PatternContainer() {

	}

	private static PatternContainer INSTANCE = new PatternContainer();

	public static PatternContainer instance() {
		return INSTANCE;
	}

	public void init(String filename) throws IOException {
		patterns = new LinkedHashMap<Pattern, String>();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				filename));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("#")) {
				continue;
			}
			try {
				String[] items = line.split("\\s+");
				if (items.length < 2) {
					continue;
				}
				String ip = items[0];
				String pattern = items[1];
				patterns.put(compileStringToPattern(pattern), ip);
				logger.info("load config success:\t" + line);
			} catch (Exception e) {
				logger.warn("parse config line error:\t" + line, e);
			}
		}
		bufferedReader.close();
	}

	private Pattern compileStringToPattern(String patternStr) {
		patternStr += ".";
		patternStr = patternStr.replace(".", "\\.");
		patternStr = patternStr.replace("*", ".*");
		patternStr += "$";
		return Pattern.compile(patternStr);
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
		if (type == Type.PTR) {
			return null;
		}
		for (Entry<Pattern, String> entry : patterns.entrySet()) {
			Matcher matcher = entry.getKey().matcher(query);
			if (matcher.matches()) {
				String answer = entry.getValue();
				AnswerCacheContainer.instance().addCache(query, type, answer);
				try {
					AnswerCacheContainer.instance().addCache(reverseIp(answer),
							Type.PTR, query);
				} catch (Throwable e) {
					logger.info("not a ip, ignored");
				}
				return answer;
			}
		}
		return null;
	}

	private String reverseIp(String ip) {
		int[] array = Address.toArray(ip);
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = array.length - 1; i >= 0; i--) {
			stringBuilder.append(array[i] + ".");
		}
		stringBuilder.append("in-addr.arpa.");
		return stringBuilder.toString();
	}
}
