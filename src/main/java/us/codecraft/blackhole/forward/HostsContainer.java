package us.codecraft.blackhole.forward;

import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 21, 2012
 */
public abstract class HostsContainer implements InitializingBean {

	private long sleepTime = TimeUnit.SECONDS.toMillis(1);

	private int timeout = 3000;

	private static final double VERY_LITTLE_DOUBLE = 1e-10;

	private static final int REFRESH_PERIOD = 30;

	private AtomicInteger refreshDeCounter = new AtomicInteger(REFRESH_PERIOD);

	private Map<SocketAddress, AveragedRequestTime> requestTimes = new ConcurrentHashMap<SocketAddress, AveragedRequestTime>();

	private Logger logger = Logger.getLogger(getClass());

	protected abstract HostTester getHostTester();

	public void clearHosts() {
		requestTimes = new ConcurrentHashMap<SocketAddress, AveragedRequestTime>();
	}

	public void addHost(SocketAddress address) {
		requestTimes.put(address, new AveragedRequestTime());
		logger.info("add dns address " + address);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(sleepTime);
						if (refreshDeCounter.get() <= 0) {
							refreshHostsTimeout();
							refreshDeCounter.set(REFRESH_PERIOD);
						}
						refreshDeCounter.decrementAndGet();
					} catch (Throwable e) {
						logger.warn("refresh error!", e);
					}
				}
			};
		}.start();
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void registerTimeCost(SocketAddress address, long timeCost) {
		requestTimes.get(address).add(timeCost);
	}

	private void refreshHostsTimeout() {
		if (logger.isInfoEnabled()) {
			logger.info("start to refresh DNS hosts");
		}
		Iterator<Entry<SocketAddress, AveragedRequestTime>> iterator = requestTimes
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<SocketAddress, AveragedRequestTime> entry = iterator.next();
			long timeCost = getHostTester().timeCost(entry.getKey());
			entry.getValue().add(timeCost);
		}
	}

	public SocketAddress getHost() {
		SocketAddress fastestHost = null;
		double minTimeCost = timeout;
		Iterator<Entry<SocketAddress, AveragedRequestTime>> iterator = requestTimes
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<SocketAddress, AveragedRequestTime> entry = iterator.next();
			if (entry.getValue().get() < minTimeCost - VERY_LITTLE_DOUBLE) {
				fastestHost = entry.getKey();
				minTimeCost = entry.getValue().get();
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("choose " + fastestHost
					+ " for least average time cost:" + minTimeCost);
		}
		if (fastestHost == null) {
			refreshDeCounter.set(0);
		}
		return fastestHost;
	}
}
