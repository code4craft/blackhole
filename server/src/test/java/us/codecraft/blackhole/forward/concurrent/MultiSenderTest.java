package us.codecraft.blackhole.forward.concurrent;

import org.junit.Before;
import org.junit.Test;

/**
 * @author yihua.huang@dianping.com
 * @date Feb 16, 2013
 */
public class MultiSenderTest {

	private MultiSender multiSender;

	@Before
	public void setUp() {
		multiSender = new MultiSender();
	}

	@Test
	public void testSend() {
		multiSender.send();
	}

}
