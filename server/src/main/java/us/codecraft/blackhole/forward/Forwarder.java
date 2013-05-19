package us.codecraft.blackhole.forward;

import org.xbill.DNS.Message;
import us.codecraft.blackhole.connector.UDPConnectionResponser;

import java.net.SocketAddress;
import java.util.List;

/**
 * @author yihua.huang@dianping.com
 * @date Jan 16, 2013
 */
public interface Forwarder {

	/**
	 * Forward query bytes to external DNS host(s) and get a valid DNS answer.
	 * 
	 * @param queryBytes
	 * @param query
	 * @return
	 */
	public void forward(final byte[] queryBytes, Message query,
			UDPConnectionResponser responser);

	/**
	 * Forward query bytes to external DNS host(s) and get a valid DNS answer.
	 * 
	 * @param queryBytes
	 * @param query
	 * @return
	 */
	public void forward(final byte[] queryBytes, Message query,
			List<SocketAddress> hosts, UDPConnectionResponser responser);

}
