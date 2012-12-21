package us.codecraft.blackhole.forward;

import java.net.SocketAddress;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 21, 2012
 */
public interface HostTester {

	public boolean isValid(SocketAddress address);

}
