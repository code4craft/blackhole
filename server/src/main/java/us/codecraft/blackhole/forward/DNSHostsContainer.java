package us.codecraft.blackhole.forward;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 21, 2012
 */
@Component
public class DNSHostsContainer {

	private int timeout = 3000;

	private Map<SocketAddress, AveragedRequestTime> requestTimes = new ConcurrentHashMap<SocketAddress, AveragedRequestTime>();

	private Logger logger = Logger.getLogger(getClass());

	public void clearHosts() {
		requestTimes = new ConcurrentHashMap<SocketAddress, AveragedRequestTime>();
	}

	public void addHost(SocketAddress address) {
		requestTimes.put(address, new AveragedRequestTime());
		logger.info("add dns address " + address);
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void registerTimeCost(SocketAddress address, long timeCost) {
		AveragedRequestTime averagedRequestTime = requestTimes.get(address);
		if (averagedRequestTime == null) {
			return;
		}
		if (timeCost >= timeout) {
			averagedRequestTime.incrFailCount();
		} else {
			averagedRequestTime.add(timeCost);
		}
	}

	public void registerFail(SocketAddress address) {
		if (requestTimes.get(address) != null) {
			requestTimes.get(address).incrFailCount();
		}
	}

	public List<SocketAddress> getAllAvaliableHosts() {
		List<SocketAddress> results = new ArrayList<SocketAddress>();
		Iterator<Entry<SocketAddress, AveragedRequestTime>> iterator = requestTimes
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<SocketAddress, AveragedRequestTime> next = iterator.next();
			if (next.getValue().get() < timeout) {
				results.add(next.getKey());
			}
		}
		return results;
	}

}
