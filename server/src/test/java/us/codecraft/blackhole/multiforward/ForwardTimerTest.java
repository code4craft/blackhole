package us.codecraft.blackhole.multiforward;

import org.junit.Test;

import us.codecraft.blackhole.forward.ConnectionTimer;

/**
 * @author yihua.huang@dianping.com
 * @date Feb 20, 2013
 */
public class ForwardTimerTest {

	private ConnectionTimer forwardTimer = new ConnectionTimer();

	@Test
	public void testCheckForwardTimeForAddress() {
		System.out.println(forwardTimer
				.checkConnectTimeForAddress("173.194.72.106"));
	}
}
