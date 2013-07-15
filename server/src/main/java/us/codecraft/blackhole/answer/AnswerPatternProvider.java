package us.codecraft.blackhole.answer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Address;
import org.xbill.DNS.Type;

/**
 * Read the config to domainPatterns and process the request record.
 * 
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class AnswerPatternProvider implements AnswerProvider {

	private DomainPatternsContainer domainPatternsContainer = new DomainPatternsContainer();

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * When the address configured as "DO_NOTHING",it will not return any
	 * address.
	 */
	public static final String DO_NOTHING = "do_nothing";
	private static final String FAKE_MX_PREFIX = "mail.";
	private static final String FAKE_CANME_PREFIX = "cname.";

	@Autowired
	private TempAnswerProvider tempAnswerContainer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.blackhole.answer.AnswerProvider#getAnswer(java.lang.String,
	 * int)
	 */
	@Override
	public String getAnswer(String query, int type) {
		if (type == Type.PTR) {
			return null;
		}
		String ip = domainPatternsContainer.getIp(query);
		if (ip == null || ip.equals(DO_NOTHING)) {
			return null;
		}
		if (type == Type.MX) {
			String fakeMXHost = fakeMXHost(query);
			tempAnswerContainer.add(fakeMXHost, Type.A, ip);
			return fakeMXHost;
		}
		if (type == Type.CNAME) {
			String fakeCNAMEHost = fakeCNAMEHost(query);
			tempAnswerContainer.add(fakeCNAMEHost, Type.A, ip);
			return fakeCNAMEHost;
		}
		try {
			tempAnswerContainer.add(reverseIp(ip), Type.PTR, query);
		} catch (Throwable e) {
			logger.info("not a ip, ignored");
		}
		return ip;
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

	public void setDomainPatternsContainer(DomainPatternsContainer domainPatternsContainer) {
		this.domainPatternsContainer = domainPatternsContainer;
	}
}
