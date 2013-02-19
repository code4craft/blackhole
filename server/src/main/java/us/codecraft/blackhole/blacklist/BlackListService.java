package us.codecraft.blackhole.blacklist;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

	private Map<String, Set<String>> invalidAddresses = new ConcurrentHashMap<String, Set<String>>();

	private Set<String> blacklist = new HashSet<String>();

	private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	public void registerInvalidAddress(Message query, String address) {
		String questionName = query.getQuestion().getName().toString();
		logger.info("register error address " + address + " for  query "
				+ questionName);
		Set<String> questionNames = invalidAddresses.get(address);
		if (questionNames == null) {
			questionNames = new HashSet<String>();
			invalidAddresses.put(address, questionNames);
		}
		questionNames.add(questionName);
		if (questionNames.size() >= 2) {
			try {
				readWriteLock.writeLock().lock();
				blacklist.add(address);
			} finally {
				readWriteLock.writeLock().unlock();
			}
		}
	}

	public void addToBlacklist(String address) {
		try {
			readWriteLock.writeLock().lock();
			blacklist.add(address);
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	public boolean inBlacklist(String address) {
		try {
			readWriteLock.readLock().lock();
			return blacklist.contains(address);
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

}
