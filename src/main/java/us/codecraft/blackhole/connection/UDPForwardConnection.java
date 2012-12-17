package us.codecraft.blackhole.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.blackhole.config.Configure;

/**
 * @author yihua.huang@dianping.com
 * @date 2012-12-15
 */
@Component
public class UDPForwardConnection {

	public static UDPForwardConnection INSTANCE = new UDPForwardConnection();

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private Configure configure;

	public byte[] forward(byte[] query) throws IOException {
		if (configure.getDnsHost() == null) {
			logger.warn("The forward DNS server is not configured!");
			return null;
		}
		logger.info("no record, forwarding to " + configure.getDnsHost() + ":"
				+ Configure.DNS_PORT);
		DatagramChannel dc = null;
		dc = DatagramChannel.open();
		SocketAddress address = new InetSocketAddress(configure.getDnsHost(),
				Configure.DNS_PORT);
		dc.connect(address);
		ByteBuffer bb = ByteBuffer.allocate(3000);

		bb.clear();
		bb.put(query);
		bb.flip();
		dc.send(bb, address);
		bb.clear();
		dc.receive(bb);
		bb.flip();
		byte[] copyOfRange = Arrays.copyOfRange(bb.array(), 0, bb.remaining());
		return copyOfRange;
	}

}
