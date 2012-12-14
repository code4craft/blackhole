package us.codecraft.blackhole.monitor;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.xbill.DNS.Address;

import us.codecraft.blackhole.server.AnswerHandler;
import us.codecraft.blackhole.zones.PatternContainer;

/**
 * 
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
public class BlackHole {

	private boolean isShutDown = false;

	private static Logger logger = Logger.getLogger(BlackHole.class);

	private TCPSocketMonitor tcpSocketMonitor;

	private UDPSocketMonitor udpSocketMonitor;

	public void start() throws UnknownHostException, IOException {
		tcpSocketMonitor = new TCPSocketMonitor(
				Address.getByAddress("0.0.0.0"), 53);
		try {
			String filename = AnswerHandler.class.getResource("/").getPath()
					+ "config/zones";
			try {
				PatternContainer.instance().init(filename);
			} catch (IOException e) {
				logger.warn("fail to load config file " + filename);
			}
		} catch (Throwable e) {
			logger.warn("");
		}
		tcpSocketMonitor.start();
		udpSocketMonitor = new UDPSocketMonitor(
				Address.getByAddress("0.0.0.0"), 53);
		udpSocketMonitor.start();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BlackHole blackHole = new BlackHole();
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
