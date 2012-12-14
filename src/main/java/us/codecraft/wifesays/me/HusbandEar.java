package us.codecraft.wifesays.me;

import org.apache.log4j.Logger;

import us.codecraft.blackhole.monitor.BlackHole;
import us.codecraft.blackhole.monitor.TCPSocketMonitor;

/**
 * @author yihua.huang@dianping.com
 * @date 2012-12-14
 */
public class HusbandEar {

	private TCPSocketMonitor tcpSocketMonitor;

	private Logger logger = Logger.getLogger(getClass());

	public void start() {
		try {
			tcpSocketMonitor = BlackHole.applicationContext
					.getBean(TCPSocketMonitor.class);
			tcpSocketMonitor.start();
		} catch (Throwable e) {
			logger.warn("init fail ", e);
		}
	}
}
