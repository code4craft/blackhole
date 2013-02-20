package us.codecraft.blackhole.selfhost;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.blackhole.cache.EhcacheClient;
import us.codecraft.blackhole.config.Configure;
import us.codecraft.blackhole.config.ZonesFileLoader;
import us.codecraft.dnstools.MacInetInetManager;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 29, 2012
 */
@Component
public class ZonesFileRefresher implements InitializingBean {

	@Autowired
	private Configure configure;

	@Autowired
	private ZonesFileLoader zonesFileLoader;

	@Autowired
	private EhcacheClient ehcacheClient;

	private ScheduledExecutorService scheduledExecutorService = Executors
			.newScheduledThreadPool(1);

	private long lastFileModifiedTime;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		File zonesFile = new File(Configure.zonesFilename);
		lastFileModifiedTime = zonesFile.lastModified();

		scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {

			public void run() {
				File zonesFile = new File(Configure.zonesFilename);
				// When two files' last modify time not equal, we consider it is
				// changed.
				synchronized (this) {
					if (zonesFile.lastModified() != lastFileModifiedTime) {
						lastFileModifiedTime = zonesFile.lastModified();
						zonesFileLoader.reload();
						MacInetInetManager macInetInetManager = MacInetInetManager
								.getInstance();
						macInetInetManager.clearDnsCache();
					}
				}
			}
		}, 500, 500, TimeUnit.MILLISECONDS);
	}
}
