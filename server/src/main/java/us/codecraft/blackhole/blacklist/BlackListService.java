package us.codecraft.blackhole.blacklist;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;

/**
 * @author yihua.huang@dianping.com
 * @date Feb 19, 2013
 */
@Component
public class BlackListService {

	private Logger logger = Logger.getLogger(getClass());

	public void registerInvalidAddress(Message query, String address) {
		logger.info("register error address " + address + " for  query "
				+ query.getQuestion().getName().toString());
	}

	public boolean inBlacklist(String address) {
		return false;
	}

}
