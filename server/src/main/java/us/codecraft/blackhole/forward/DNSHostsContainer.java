package us.codecraft.blackhole.forward;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 21, 2012
 */
@Component
public class DNSHostsContainer {

    private int timeout = 3000;

    private int order;

    private Map<SocketAddress, Integer> requestTimes = new ConcurrentHashMap<SocketAddress, Integer>();

    private Logger logger = Logger.getLogger(getClass());

    public void clearHosts() {
        requestTimes = new ConcurrentHashMap<SocketAddress, Integer>();
        order = 0;
    }

    public void addHost(SocketAddress address) {
        requestTimes.put(address, order++);
        logger.info("add dns address " + address);
    }

    public int getOrder(SocketAddress socketAddress) {
        Integer order = requestTimes.get(socketAddress);
        return order==null?0:order;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public List<SocketAddress> getAllAvaliableHosts() {
        List<SocketAddress> results = new ArrayList<SocketAddress>();
        Iterator<Entry<SocketAddress, Integer>> iterator = requestTimes
                .entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<SocketAddress, Integer> next = iterator.next();
            results.add(next.getKey());
        }
        return results;
    }

}
