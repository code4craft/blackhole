package us.codecraft.blackhole.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Address;

import us.codecraft.blackhole.utils.RecordUtils;
import us.codecraft.blackhole.zones.AnswerPatternContainer;
import us.codecraft.blackhole.zones.NSPatternContainer;
import us.codecraft.wifesays.me.ReloadAble;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 28, 2012
 */
@Component
public class ZonesFileLoader implements InitializingBean, ReloadAble {

	@Autowired
	private Configure configure;

	@Autowired
	private AnswerPatternContainer answerPatternContainer;

	@Autowired
	private NSPatternContainer nsPatternContainer;

	private Logger logger = Logger.getLogger(getClass());

	public void readConfig(String filename) {
		try {
			Map<Pattern, String> answerPatternsTemp = new LinkedHashMap<Pattern, String>();
			Map<Pattern, String> nsPatternsTemp = new LinkedHashMap<Pattern, String>();
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
					if (items[0].equalsIgnoreCase("NS")) {
						boolean configIp = RecordUtils
								.areValidIpv4Addresses(items[1]);
						String ip = configIp ? items[1] : "";
						for (int i = configIp ? 2 : 1; i < items.length; i++) {
							String pattern = items[i];
							// ip format check
							Pattern compileStringToPattern = compileStringToPattern(pattern);
							nsPatternsTemp.put(compileStringToPattern, ip);
							answerPatternsTemp.put(compileStringToPattern,
									AnswerPatternContainer.DO_NOTHING);
						}

					} else {
						String ip = items[0];
						for (int i = 1; i < items.length; i++) {
							String pattern = items[i];
							// ip format check
							Address.getByAddress(ip);
							answerPatternsTemp.put(
									compileStringToPattern(pattern), ip);
						}
					}

					logger.info("read config success:\t" + line);
				} catch (Exception e) {
					logger.warn("parse config line error:\t" + line + "\t" + e);
				}
			}
			answerPatternContainer.setPatterns(answerPatternsTemp);
			nsPatternContainer.setPatterns(nsPatternsTemp);
			bufferedReader.close();
		} catch (Throwable e) {
			logger.warn("read config file failed:" + filename, e);
		}
	}

	private Pattern compileStringToPattern(String patternStr) {
		patternStr = "^" + patternStr;
		patternStr += ".";
		patternStr = patternStr.replace(".", "\\.");
		patternStr = patternStr.replace("*", ".*");
		patternStr += "$";
		return Pattern.compile(patternStr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.ReloadAble#reload()
	 */
	@Override
	public void reload() {
		readConfig(Configure.getZonesFilename());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		reload();
	}

}
