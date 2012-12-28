package us.codecraft.blackhole.zones;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Address;
import org.xbill.DNS.Type;

/**
 * Read the config to patterns and process the request record.
 * 
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class PatternContainer implements AnswerProvider {

	private volatile Map<Pattern, String> patterns;

	private Logger logger = Logger.getLogger(getClass());

	private static final String FAKE_MX_PREFIX = "mail.";
	private static final String FAKE_CANME_PREFIX = "cname.";

	@Autowired
	private AnswerContainer answerContainer;

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
			if (matcher.find()) {
				String answer = entry.getValue();
				if (type == Type.MX) {
					String fakeMXHost = fakeMXHost(query);
					answerContainer.add(fakeMXHost, Type.A, answer);
					return fakeMXHost;
				}
				if (type == Type.CNAME) {
					String fakeCNAMEHost = fakeCNAMEHost(query);
					answerContainer.add(fakeCNAMEHost, Type.A, answer);
					return fakeCNAMEHost;
				}
				try {
					answerContainer.add(reverseIp(answer), Type.PTR, query);
				} catch (Throwable e) {
					logger.info("not a ip, ignored");
				}
				return answer;
			}
		}
		return null;
	}

	/**
	 * generate a fake MX host
	 * 
	 * @param domain
	 * @return
	 */
	private String fakeMXHost(String domain) {
		return FAKE_MX_PREFIX + domain;
	}

	/**
	 * 
	 * @param domain
	 * @return
	 */
	private String fakeCNAMEHost(String domain) {
		return FAKE_CANME_PREFIX + domain;
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

	/**
	 * @param patterns
	 *            the patterns to set
	 */
	public void setPatterns(Map<Pattern, String> patterns) {
		this.patterns = patterns;
	}

}
