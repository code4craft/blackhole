package us.codecraft.blackhole.forward.concurrent;

import org.junit.Before;
import org.junit.Test;

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

	@Test
	public void testReceive() {
		multireReceiver.receive();
	}

}
