package us.codecraft.blackhole.config;

import org.springframework.stereotype.Component;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class Configure {
	public final static long DEFAULT_TTL = 2000;
	public final static int DEFAULT_DNS_TIMEOUT = 2000;
	public static String FILE_PATH = "/usr/local/blackhole/";

	public final static int DEFAULT_MX_PRIORY = 10;

	private long ttl = DEFAULT_TTL;

	private int mxPriory = DEFAULT_MX_PRIORY;

	public final static int DNS_PORT = 53;

	private int dnsTimeOut = DEFAULT_DNS_TIMEOUT;

	private String loggerLevel;

	private boolean useCache;

	private String configFilename = Configure.FILE_PATH
			+ "/config/blackhole.conf";

	private String zonesFilename = Configure.FILE_PATH + "/config/zones";

	/**
	 * @return the configFilename
	 */
	public String getConfigFilename() {
		return configFilename;
	}

	/**
	 * @param configFilename
	 *            the configFilename to set
	 */
	public void setConfigFilename(String configFilename) {
		this.configFilename = configFilename;
	}

	/**
	 * @return the zonesFilename
	 */
	public String getZonesFilename() {
		return zonesFilename;
	}

	/**
	 * @param zonesFilename
	 *            the zonesFilename to set
	 */
	public void setZonesFilename(String zonesFilename) {
		this.zonesFilename = zonesFilename;
	}

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
	 * @return the dnsTimeOut
	 */
	public int getDnsTimeOut() {
		return dnsTimeOut;
	}

	/**
	 * @return the loggerLevel
	 */
	public String getLoggerLevel() {
		return loggerLevel;
	}

	/**
	 * @param loggerLevel
	 *            the loggerLevel to set
	 */
	public void setLoggerLevel(String loggerLevel) {
		this.loggerLevel = loggerLevel;
	}

	/**
	 * @param dnsTimeOut
	 *            the dnsTimeOut to set
	 */
	public void setDnsTimeOut(int dnsTimeOut) {
		this.dnsTimeOut = dnsTimeOut;
	}

	/**
	 * @param useCache
	 *            the useCache to set
	 */
	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

}
