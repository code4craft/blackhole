package us.codecraft.blackhole.connector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.blackhole.config.Configure;

/**
 * WTF ugly concurrent coding style! Fortunately it works!<br/>
 * 
 * 
 * @author yihua.huang@dianping.com
 * @date 2012-12-15
 */
@Component
public class UDPForwardConnection {

	public static UDPForwardConnection INSTANCE = new UDPForwardConnection();

	private Logger logger = Logger.getLogger(getClass());

	private volatile ExecutorService executor;

	private ExecutorService getExecutor() {
		if (executor == null) {
			synchronized (this) {
				executor = Executors.newFixedThreadPool(50);
			}
		}
		return executor;
	}

	@Autowired
	private Configure configure;

	public byte[] forward(final byte[] query) {
		if (configure.getDnsHost() == null) {
			logger.warn("The forward DNS server is not configured!");
			return null;
		}
		byte[] result = null;

		FutureTask<Object> future = new FutureTask<Object>(
				new Callable<Object>() {
					public Object call() throws IOException {
						return forward0(query);
					}
				});
		getExecutor().execute(future);
		try {
			result = (byte[]) future.get(configure.getDnsTimeOut(),
					TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			future.cancel(true);
			logger.warn("forward error " + e);
		}
		return result;
	}

	private byte[] forward0(byte[] query) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("no record, forwarding to " + configure.getDnsHost()
					+ ":" + Configure.DNS_PORT);
		}
		DatagramChannel dc = null;
		dc = DatagramChannel.open();
		SocketAddress address = new InetSocketAddress(configure.getDnsHost(),
				Configure.DNS_PORT);
		ByteBuffer bb = ByteBuffer.allocate(512);

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
