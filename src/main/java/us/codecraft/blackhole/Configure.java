package us.codecraft.blackhole;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
public final class Configure {

	public final static long DEFAULT_TTL = 1000;

	private long ttl = DEFAULT_TTL;

	public final static int DEFAULT_MX_PRIORY = 10;

	private int mxPriory = DEFAULT_MX_PRIORY;

	private Configure() {
	}

	private final static Configure INSTANCE = new Configure();

	public static Configure instance() {
		return INSTANCE;
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

}
