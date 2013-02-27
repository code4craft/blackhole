package us.codecraft.blackhole.multiforward;

import java.io.IOException;
import java.net.InetAddress;

import org.junit.Before;
import org.junit.Test;

import us.codecraft.blackhole.forward.MultiUDPReceiver;

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
	public void testReach() throws IOException {
		InetAddress inetAddress = InetAddress.getByName("203.161.230.171");
		System.out.println(inetAddress.isReachable(1000));
	}

}
