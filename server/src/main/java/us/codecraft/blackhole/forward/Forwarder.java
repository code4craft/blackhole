package us.codecraft.blackhole.forward;

import java.net.SocketAddress;
import java.util.List;

import org.xbill.DNS.Message;

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
	public byte[] forward(final byte[] queryBytes, Message query);

	/**
	 * Forward query bytes to external DNS host(s) and get a valid DNS answer.
	 * 
	 * @param queryBytes
	 * @param query
	 * @return
	 */
	public byte[] forward(final byte[] queryBytes, Message query,
			List<SocketAddress> hosts);

}
