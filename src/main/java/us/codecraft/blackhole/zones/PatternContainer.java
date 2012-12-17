package us.codecraft.blackhole.zones;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Address;
import org.xbill.DNS.Type;

import us.codecraft.blackhole.config.Configure;
import us.codecraft.wifesays.me.ReloadAble;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class PatternContainer implements AnswerProvider, InitializingBean,
		ReloadAble {

	private volatile Map<Pattern, String> patterns;

	private Logger logger = Logger.getLogger(getClass());

	private String filename = Configure.FILE_PATH + "/config/zones";

	@Autowired
	private AnswerCacheContainer answerCacheContainer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		readConfig(filename);

	}

	public void readConfig(String filename) {
		try {
			Map<Pattern, String> patternsTemp = new LinkedHashMap<Pattern, String>();
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
					patternsTemp.put(compileStringToPattern(pattern), ip);
					logger.info("read config success:\t" + line);
				} catch (Exception e) {
					logger.warn("parse config line error:\t" + line, e);
				}
			}
			patterns = patternsTemp;
			bufferedReader.close();
		} catch (Throwable e) {
			logger.warn("read config file failed:" + filename, e);
		}
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
				answerCacheContainer.addCache(query, type, answer);
				try {
					answerCacheContainer.addCache(reverseIp(answer), Type.PTR,
							query);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.ReloadAble#reload()
	 */
	@Override
	public void reload() {
		readConfig(filename);
	}
}
