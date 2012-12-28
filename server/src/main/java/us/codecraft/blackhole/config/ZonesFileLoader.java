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

import us.codecraft.blackhole.zones.PatternContainer;
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
	private PatternContainer patternContainer;

	private Logger logger = Logger.getLogger(getClass());

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
					// ip format check
					Address.getByAddress(ip);
					patternsTemp.put(compileStringToPattern(pattern), ip);
					logger.info("read config success:\t" + line);
				} catch (Exception e) {
					logger.warn("parse config line error:\t" + line + "\t" + e);
				}
			}
			patternContainer.setPatterns(patternsTemp);
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
	 * @see us.codecraft.wifesays.me.ReloadAble#reload()
	 */
	@Override
	public void reload() {
		readConfig(configure.getZonesFilename());
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
