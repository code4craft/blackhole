package us.codecraft.blackhole.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.blackhole.answer.AnswerPatternProvider;
import us.codecraft.blackhole.answer.CustomAnswerPatternProvider;
import us.codecraft.blackhole.answer.DomainPatternsContainer;
import us.codecraft.blackhole.cache.CacheManager;
import us.codecraft.blackhole.forward.DNSHostsContainer;
import us.codecraft.blackhole.utils.DoubleKeyMap;
import us.codecraft.wifesays.me.ReloadAble;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 28, 2012
 */
@Component
public class ZonesFileLoader implements InitializingBean, ReloadAble {

	@Autowired
	private Configure configure;

	@Autowired
	private AnswerPatternProvider answerPatternContainer;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private CustomAnswerPatternProvider customAnswerPatternProvider;

    @Autowired
    private DNSHostsContainer dnsHostsContainer;

	private Logger logger = Logger.getLogger(getClass());

	public void readConfig(String filename) {
		try {
			DomainPatternsContainer domainPatternsContainer = new DomainPatternsContainer();
            DomainPatternsContainer nsDomainPatternContainer = new DomainPatternsContainer();
			DoubleKeyMap<String, Pattern, String> customAnswerPatternsTemp = new DoubleKeyMap<String, Pattern, String>(
					new ConcurrentHashMap<String, Map<Pattern, String>>(), LinkedHashMap.class);
			DoubleKeyMap<String, String, String> customAnswerTextsTemp = new DoubleKeyMap<String, String, String>(
					new ConcurrentHashMap<String, Map<String, String>>(), HashMap.class);
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				ZonesPattern zonesPattern = ZonesPattern.parse(line);
				if (zonesPattern == null) {
					continue;
				}
				try {
					if (zonesPattern.getUserIp() == null) {
						for (Pattern pattern : zonesPattern.getPatterns()) {
							domainPatternsContainer.getDomainPatterns().put(pattern, zonesPattern.getTargetIp());
						}
						for (String text : zonesPattern.getTexts()) {
							domainPatternsContainer.getDomainTexts().put(text, zonesPattern.getTargetIp());
						}
					} else {
						for (Pattern pattern : zonesPattern.getPatterns()) {
							customAnswerPatternsTemp.put(zonesPattern.getUserIp(), pattern, zonesPattern.getTargetIp());
						}
						for (String text : zonesPattern.getTexts()) {
							customAnswerTextsTemp.put(zonesPattern.getUserIp(), text, zonesPattern.getTargetIp());
						}
					}
					logger.info("read config success:\t" + line);
				} catch (Exception e) {
					logger.warn("parse config line error:\t" + line + "\t" , e);
				}
				// For NS
				if (line.startsWith("NS")) {
					line = StringUtils.removeStartIgnoreCase(line, "NS").trim();
                    zonesPattern = ZonesPattern.parse(line);
                    if (zonesPattern == null) {
                        continue;
                    }
                    try {
                        for (Pattern pattern : zonesPattern.getPatterns()) {
                            nsDomainPatternContainer.getDomainPatterns().put(pattern,zonesPattern.getTargetIp());
                        }
                        for (String text : zonesPattern.getTexts()) {
                            nsDomainPatternContainer.getDomainTexts().put(text,zonesPattern.getTargetIp());
                        }
                        logger.info("read config success:\t" + line);
                    } catch (Exception e) {
                        logger.warn("parse config line error:\t" + line + "\t" + e);
                    }
				}
			}
			answerPatternContainer.setDomainPatternsContainer(domainPatternsContainer);
			customAnswerPatternProvider.setDomainPatterns(customAnswerPatternsTemp);
			customAnswerPatternProvider.setDomainTexts(customAnswerTextsTemp);
            dnsHostsContainer.setDomainPatternsContainer(nsDomainPatternContainer);
			bufferedReader.close();
		} catch (Throwable e) {
			logger.warn("read config file failed:" + filename, e);
		}
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
