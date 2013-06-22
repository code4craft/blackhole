package us.codecraft.blackhole.config;

import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

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

	private boolean enableSafeBox = true;

	private SocketAddress fakeDnsServer;

	private boolean useCache = true;

	private int threadNum = 4;

	private static String configFilename;

    private int cacheExpire;

	public static String getConfigFilename() {
		if (configFilename == null) {
			return Configure.FILE_PATH + "/config/blackhole.conf";
		} else {
			return configFilename;
		}
	}

	public static void setConfigFilename(String name) {
		configFilename = name;
	}

	private static String zonesFilename;

	public static void setZonesFilename(String name) {
		zonesFilename = name;
	}

	public static String getZonesFilename() {
		if (zonesFilename == null) {
			return Configure.FILE_PATH + "/config/zones";
		} else {
			return zonesFilename;
		}
	}

	public boolean isEnableSafeBox() {
		return enableSafeBox;
	}

	public void setEnableSafeBox(boolean enableSafeBox) {
		this.enableSafeBox = enableSafeBox;
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

	public SocketAddress getFakeDnsServer() {
		return fakeDnsServer;
	}

	public void setFakeDnsServer(String fakeDnsServer) {
		this.fakeDnsServer = new InetSocketAddress(fakeDnsServer, DNS_PORT);
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

    public int getCacheExpire() {
        return cacheExpire;
    }

    public void setCacheExpire(int cacheExpire) {
        this.cacheExpire = cacheExpire;
    }
}
