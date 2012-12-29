package us.codecraft.blackhole.selfhost;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.blackhole.config.ConfigFileLoader;
import us.codecraft.blackhole.config.Configure;
import us.codecraft.blackhole.config.ZonesFileLoader;
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
	private ConfigFileLoader configFileLoader;

	@Autowired
	private ZonesFileLoader zonesFileLoader;

	@Autowired
	private DNSHostsContainer dnsHostsContainer;

	@Autowired
	private Configure configure;

	public void start() {
		try {

			initBlackHoleDns();
			blackHole.start();
			setDns();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initBlackHoleDns() {
		for (String dnsServer : inetConnectinoProperties.getDnsServer()) {
			if (!dnsServer.equals("127.0.0.1")) {
				dnsHostsContainer.addHost(new InetSocketAddress(dnsServer,
						Configure.DNS_PORT));
			}
		}
		configFileLoader.setReloadOff(true);
	}

	private void setDns() {
		if (inetConnectinoProperties.getDnsServer().contains("127.0.0.1")) {
			inetConnectinoProperties.getDnsServer().remove("127.0.0.1");
		}
		inetConnectinoProperties.getDnsServer().add(0, "127.0.0.1");
		MacInetInetManager macInetInetManager = MacInetInetManager
				.getInstance();
		macInetInetManager.setConnectionDns(inetConnectinoProperties);
		macInetInetManager.clearDnsCache();
		inetConnectinoProperties.getDnsServer().remove(0);
	}

	private void setDnsBack() {
		MacInetInetManager macInetInetManager = MacInetInetManager
				.getInstance();
		macInetInetManager.setConnectionDns(inetConnectinoProperties);
		macInetInetManager.clearDnsCache();
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
