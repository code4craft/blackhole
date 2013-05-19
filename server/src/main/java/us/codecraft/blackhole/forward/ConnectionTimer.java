package us.codecraft.blackhole.forward;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;
import us.codecraft.blackhole.antipollution.SafeHostManager;
import us.codecraft.blackhole.cache.CacheManager;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Check the connecting time of host.Use to detect whether the DNS answer is
 * correct.
 * 
 * @author yihua.huang@dianping.com
 * @date Feb 20, 2013
 */
@Component
public class ConnectionTimer {

	@Autowired
	private SafeHostManager safeBoxService;
	/**
	 * If the server doesn't support ICMP protocol, try http instead.
	 */
	private static final int PORT_FOR_TEST = 80;

	@Autowired
	private CacheManager cacheManager;

	/**
	 * Time out for connection timer.
	 */
	private static final int TIME_OUT = 1000;
	/**
	 * expire time for connect time cache.
	 */
	private static final int EXPIRE_TIME = 3600 * 1000 * 5;

	private ExecutorService checkExecutors = Executors.newFixedThreadPool(4);

	private Logger logger = Logger.getLogger(getClass());

	public void checkConnectTimeForAnswer(final Message query,
			final Message message) {
		checkExecutors.submit(new Runnable() {

			@Override
			public void run() {
				checkConnectTimeForAnswer0(query, message);
			}
		});
	}

	private void checkConnectTimeForAnswer0(Message query, Message message) {
		byte[] answerBytes = cacheManager.getResponseFromCache(query);
		Message answerInCache = null;
		if (answerBytes != null) {
			try {
				answerInCache = new Message(answerBytes);
				MessageCheckResult checkConnectionTime = checkConnectionTime(answerInCache);
				if (checkConnectionTime == MessageCheckResult.UNCHANGED) {
					return;
				} else if (checkConnectionTime == MessageCheckResult.CHANGEDE_BUT_USEFUL) {
					// remove unreachable address and save
					if (logger.isDebugEnabled()) {
						logger.debug("update record in cahce " + message);
					}
					cacheManager.setResponseToCache(query, answerInCache.toWire());
					return;
				}
			} catch (IOException e) {
			}
		}
		MessageCheckResult checkConnectionTime = checkConnectionTime(message);
		if (checkConnectionTime != MessageCheckResult.ALL_TIMEOUT) {
			if (logger.isDebugEnabled()) {
				logger.debug("set new message to cahce " + message);
			}
			cacheManager.setResponseToCache(query, message.toWire());
		}
	}

	private enum MessageCheckResult {
		ALL_TIMEOUT, CHANGEDE_BUT_USEFUL, UNCHANGED;
	}

	private MessageCheckResult checkConnectionTime(Message message) {
		boolean changed = false;
		Record[] answers = message.getSectionArray(Section.ANSWER);
		for (Record answer : answers) {
			if (answer.getType() == Type.A || answer.getType() == Type.AAAA
					|| answer.getType() == Type.CNAME) {
				String address = StringUtils.removeEnd(answer.rdataToString(),
						".");
				long connectTime = getConnectTime(address);
				if (connectTime >= TIME_OUT) {
					changed = true;
					message.removeRecord(answer, Section.ANSWER);
				} else {
					String domain = StringUtils.removeEnd(message.getQuestion()
							.getName().toString(), ".");
					if (safeBoxService.isPoisoned(domain)
							&& safeBoxService.get(domain) == null
							&& (answer.getType() == Type.A || answer.getType() == Type.AAAA)) {
						safeBoxService.add(domain, address);
					}
				}
			}
		}
		if (message.getSectionArray(Section.ANSWER).length == 0) {
			return MessageCheckResult.ALL_TIMEOUT;
		} else if (changed) {

			return MessageCheckResult.CHANGEDE_BUT_USEFUL;
		} else {
			return MessageCheckResult.UNCHANGED;
		}
	}

	private long getConnectTime(String address) {
		Long connectTime = cacheManager.<Long> get(address);
		if (connectTime == null) {
			connectTime = checkConnectTimeForAddress(address);
			cacheManager.set(address, connectTime, EXPIRE_TIME);
		}
		return connectTime;
	}

	public long checkConnectTimeForAddress(String address) {
		Socket socket = null;
		try {
			long startTime = System.currentTimeMillis();
			boolean icmpReachable = InetAddress.getByName(address).isReachable(
					TIME_OUT);
			long timeCost = System.currentTimeMillis() - startTime;
			if (icmpReachable) {
				return timeCost;
			}
			socket = new Socket();
			startTime = System.currentTimeMillis();
			socket.connect(new InetSocketAddress(address, PORT_FOR_TEST),
					TIME_OUT);
			timeCost = System.currentTimeMillis() - startTime;
			socket.close();
			return timeCost;
		} catch (UnknownHostException e) {
			logger.warn("unkown host " + address + " " + e);
		} catch (SocketTimeoutException e) {
		} catch (SocketException e) {
		} catch (IOException e) {
			logger.warn("connect " + address + " error " + e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		return TIME_OUT;
	}
}