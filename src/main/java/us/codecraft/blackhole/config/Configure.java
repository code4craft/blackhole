package us.codecraft.blackhole.config;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import us.codecraft.wifesays.me.ReloadAble;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class Configure implements ReloadAble, InitializingBean {

	public final static long DEFAULT_TTL = 2000;
	public final static int DEFAULT_DNS_TIMEOUT = 2000;

	private long ttl = DEFAULT_TTL;

	public final static int DEFAULT_MX_PRIORY = 10;

	private int mxPriory = DEFAULT_MX_PRIORY;

	public static String FILE_PATH = "/usr/local/blackhole/";

	private String filename = FILE_PATH + "/config/blackhole.conf";

	public final static int DNS_PORT = 53;

	private Logger logger = Logger.getLogger(getClass());

	private String dnsHost;

	private int dnsTimeOut = DEFAULT_DNS_TIMEOUT;

	@SuppressWarnings("unused")
	private String loggerLevel;

	private boolean useCache;

	/**
	 * @return the useCache
	 */
	public boolean isUseCache() {
		return useCache;
	}

	/**
	 * 
	 * @return
	 */
	public long getTTL() {
		return ttl;
	}

	/**
	 * @return the ttl
	 */
	public long getTtl() {
		return ttl;
	}

	/**
	 * @param ttl
	 *            the ttl to set
	 */
	public void setTtl(long ttl) {
		this.ttl = ttl;
	}

	/**
	 * @return the mxPriory
	 */
	public int getMxPriory() {
		return mxPriory;
	}

	/**
	 * @param mxPriory
	 *            the mxPriory to set
	 */
	public void setMxPriory(int mxPriory) {
		this.mxPriory = mxPriory;
	}

	/**
	 * @return the dnsHost
	 */
	public String getDnsHost() {
		return dnsHost;
	}

	/**
	 * @param dnsHost
	 *            the dnsHost to set
	 */
	public void setDnsHost(String dnsHost) {
		this.dnsHost = dnsHost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.ReloadAble#reload()
	 */
	@Override
	public void reload() {
		readConfig(filename);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		readConfig(filename);
	}

	public void readConfig(String filename) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					filename));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("#")) {
					continue;
				}
				try {
					String[] items = line.split("=");
					if (items.length < 2) {
						continue;
					}
					String key = items[0];
					String value = items[1];
					boolean configed = config(key, value);
					if (configed) {
						logger.info("read config success:\t" + line);
					}
				} catch (Exception e) {
					logger.warn("parse config line error:\t" + line, e);
				}
			}
			bufferedReader.close();
		} catch (Throwable e) {
			logger.warn("read config file failed:" + filename, e);
		}
	}

	private void configLogLevel(String value) {
		Logger rootLogger = Logger.getRootLogger();
		if ("debug".equalsIgnoreCase(value)) {
			rootLogger.setLevel(Level.DEBUG);
		} else if ("info".equalsIgnoreCase(value)) {
			rootLogger.setLevel(Level.INFO);
		} else if ("warn".equalsIgnoreCase(value)) {
			rootLogger.setLevel(Level.WARN);
		} else {
			// unsupported level
			return;
		}
		loggerLevel = value;

	}

	/**
	 * @return the dnsTimeOut
	 */
	public int getDnsTimeOut() {
		return dnsTimeOut;
	}

	private boolean config(String key, String value) {
		if (key.equalsIgnoreCase("ttl")) {
			ttl = Integer.parseInt(value);
		} else if (key.equalsIgnoreCase("dns")) {
			dnsHost = value;
		} else if (key.equalsIgnoreCase("cache")) {
			useCache = BooleanUtils.toBooleanObject(value);
		} else if (key.equalsIgnoreCase("log")) {
			configLogLevel(value);
		} else if (key.equalsIgnoreCase("dns_timeout")) {
			dnsTimeOut = Integer.parseInt(value);
		} else {
			return false;
		}
		return true;
	}
}
