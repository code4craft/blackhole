package us.codecraft.blackhole.utils;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 20, 2012
 */
public class StatefulLock {

	private int state;

	public StatefulLock(int state) {
		this.state = state;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}

}
