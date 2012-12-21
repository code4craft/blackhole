package us.codecraft.blackhole.forward;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 21, 2012
 */
public abstract class HostsContainer implements InitializingBean {

	private final static int MAX_TRIED_TIMES = 5;

	private List<SocketAddress> hosts = Collections
			.synchronizedList(new ArrayList<SocketAddress>());

	private Map<SocketAddress, Boolean> isValidMap = new ConcurrentHashMap<SocketAddress, Boolean>();

	private Map<SocketAddress, AtomicInteger> triedTimeMap = new ConcurrentHashMap<SocketAddress, AtomicInteger>();

	private ScheduledExecutorService dnsTesterScheduler = Executors
			.newScheduledThreadPool(1);

	private Logger logger = Logger.getLogger(getClass());

	protected abstract HostTester getHostTester();

	public void clearHosts() {
		hosts.clear();
	}

	public void addHost(SocketAddress address) {
		if (!hosts.contains(address)) {
			logger.info("add dns address " + address);
			hosts.add(address);
		}
	}

	private boolean isValid(SocketAddress address) {
		if (isValidMap.get(address) == null) {
			isValidMap.put(address, Boolean.TRUE);
			return true;
		} else {
			return isValidMap.get(address);
		}
	}

	private int incrementAndGetTriedTime(SocketAddress address) {
		if (triedTimeMap.get(address) == null) {
			triedTimeMap.put(address, new AtomicInteger());
		}
		return triedTimeMap.get(address).incrementAndGet();
	}

	public void registerFail(SocketAddress address) {
		int incrementAndGetTriedTime = incrementAndGetTriedTime(address);
		if (incrementAndGetTriedTime > MAX_TRIED_TIMES) {
			logger.warn("Failed too many times, ignore the server. " + address);
			isValidMap.put(address, Boolean.FALSE);
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
		dnsTesterScheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				checkValidHosts();
			}
		}, 1, 1, TimeUnit.MINUTES);
	}

	private void checkValidHosts() {
		Iterator<SocketAddress> iterator = hosts.iterator();
		while (iterator.hasNext()) {
			SocketAddress address = iterator.next();
			if (!isValid(address)) {
				logger.debug("check host " + address);
				boolean isValid = getHostTester().isValid(address);
				if (isValid) {
					triedTimeMap.put(address, new AtomicInteger());
				}
				isValidMap.put(address, isValid);
			}
		}
	}

	public SocketAddress getHost() {
		Iterator<SocketAddress> iterator = hosts.iterator();
		while (iterator.hasNext()) {
			SocketAddress address = iterator.next();
			if (isValid(address)) {
				return address;
			}
		}
		return null;
	}

}
