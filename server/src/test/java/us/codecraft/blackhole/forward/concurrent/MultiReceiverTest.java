package us.codecraft.blackhole.forward.concurrent;

import org.junit.Before;

/**
 * @author yihua.huang@dianping.com
 * @date Feb 16, 2013
 */
public class MultiReceiverTest {

	private MultiUDPReceiver multireReceiver;

	@Before
	public void setUp() {
		multireReceiver = new MultiUDPReceiver();
	}

}
