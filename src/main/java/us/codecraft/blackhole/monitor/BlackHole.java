package us.codecraft.blackhole.monitor;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class BlackHole {

	private boolean isShutDown = false;

	public static ApplicationContext applicationContext;

	private static Logger logger = Logger.getLogger(BlackHole.class);

	private TCPSocketMonitor tcpSocketMonitor;

	private UDPSocketMonitor udpSocketMonitor;

	public void start() throws UnknownHostException, IOException {
		tcpSocketMonitor = applicationContext.getBean(TCPSocketMonitor.class);
		tcpSocketMonitor.start();
		udpSocketMonitor = applicationContext.getBean(UDPSocketMonitor.class);
		udpSocketMonitor.start();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		applicationContext = new ClassPathXmlApplicationContext(
				"spring/applicationContext*.xml");
		BlackHole blackHole = applicationContext.getBean(BlackHole.class);
		try {
			blackHole.start();
		} catch (UnknownHostException e) {
			logger.warn("init failed ", e);
		} catch (IOException e) {
			logger.warn("init failed ", e);
		}
		while (!blackHole.isShutDown) {
			try {
				Thread.sleep(10000000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
