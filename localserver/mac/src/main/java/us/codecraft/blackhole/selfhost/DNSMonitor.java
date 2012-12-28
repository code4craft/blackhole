package us.codecraft.blackhole.selfhost;

import java.io.IOException;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.blackhole.forward.DNSHostsContainer;
import us.codecraft.blackhole.monitor.BlackHole;
import us.codecraft.dnstools.InetConnectinoProperties;
import us.codecraft.dnstools.MacInetInetManager;
import us.codecraft.wifesays.me.ShutDownAble;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 27, 2012
 */
@Component
public class DNSMonitor implements ShutDownAble {

	private InetConnectinoProperties inetConnectinoProperties;

	@Autowired
	private BlackHole blackHole;

	@Autowired
	private DNSHostsContainer dnsHostsContainer;

	public void start() {
		setDns();
		try {
			blackHole.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setDns() {
		inetConnectinoProperties.getDnsServer().add(0, "127.0.0.1");
		MacInetInetManager macInetInetManager = new MacInetInetManager();
		macInetInetManager.setConnectionDns(inetConnectinoProperties);
		inetConnectinoProperties.getDnsServer().remove(0);
	}

	private void setDnsBack() {
		MacInetInetManager macInetInetManager = new MacInetInetManager();
		macInetInetManager.setConnectionDns(inetConnectinoProperties);
	}

	/**
	 * @return the inetConnectinoProperties
	 */
	public InetConnectinoProperties getInetConnectinoProperties() {
		return inetConnectinoProperties;
	}

	/**
	 * @param inetConnectinoProperties
	 *            the inetConnectinoProperties to set
	 */
	public void setInetConnectinoProperties(
			InetConnectinoProperties inetConnectinoProperties) {
		this.inetConnectinoProperties = inetConnectinoProperties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.ShutDownAble#shutDown()
	 */
	public void shutDown() {
		setDnsBack();
	}

}
