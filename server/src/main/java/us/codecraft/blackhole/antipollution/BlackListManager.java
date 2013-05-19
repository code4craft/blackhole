package us.codecraft.blackhole.antipollution;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;
import us.codecraft.blackhole.config.Configure;
import us.codecraft.wifesays.me.ShutDownAble;
import us.codecraft.wifesays.me.StandReadyWorker;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author yihua.huang@dianping.com
 * @date Feb 19, 2013
 */
@Component
public class BlackListManager extends StandReadyWorker implements
		InitializingBean, ShutDownAble {

	private Logger logger = Logger.getLogger(getClass());

	private Map<String, Set<String>> invalidAddresses = new ConcurrentHashMap<String, Set<String>>();

	private Set<String> blacklist = new HashSet<String>();

	private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	private static final String FLUSH_CMD = "flush";

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

	public void flushToFile(String filename) throws IOException {
		PrintWriter writer = new PrintWriter(new File(filename));
		for (String address : blacklist) {
			writer.println(address);
		}
		writer.close();
	}

	public void loadFromFile(String filename) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				new File(filename)));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			line = line.trim();
			if (logger.isDebugEnabled()) {
				logger.debug("load blacklist address " + line);
			}
			blacklist.add(line);
		}
		bufferedReader.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.ShutDownAble#shutDown()
	 */
	@Override
	public void shutDown() {
		String filename = Configure.FILE_PATH + "/blacklist";
		try {
			flushToFile(filename);
		} catch (IOException e) {
			logger.warn("write to file " + filename + " error! " + e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		String filename = Configure.FILE_PATH + "/blacklist";
		try {
			loadFromFile(filename);
		} catch (IOException e) {
			logger.warn("load file " + filename + " error! " + e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.wifesays.me.StandReady#doWhatYouShouldDo(java.lang.String)
	 */
	@Override
	public String doWhatYouShouldDo(String whatWifeSays) {
		if (FLUSH_CMD.equalsIgnoreCase(whatWifeSays)) {
			String filename = Configure.FILE_PATH + "/blacklist";
			try {
				flushToFile(filename);
			} catch (IOException e) {
				logger.warn("write to file " + filename + " error! " + e);
			}
			return "SUCCESS";
		}
		return null;
	}

}
