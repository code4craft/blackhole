package us.codecraft.blackhole.forward;

import java.io.IOException;
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
import org.xbill.DNS.Message;

import us.codecraft.blackhole.config.Configure;
import us.codecraft.blackhole.utils.RecordUtils;

/**
 * 
 * @author yihua.huang@dianping.com
 * @date 2012-12-15
 */
@Component
public class UDPForwardConnector {

	public static UDPForwardConnector INSTANCE = new UDPForwardConnector();

	private Logger logger = Logger.getLogger(getClass());

	private volatile ExecutorService executor;

	@Autowired
	private DNSHostsContainer dnsHostsContainer;

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

	public byte[] forward(final byte[] queryBytes, Message query) {
		return forwardToAddress(queryBytes, query, dnsHostsContainer.getHost());
	}

	public byte[] forwardDummy(final byte[] queryBytes,
			final SocketAddress address) {
		return forwardToAddress(queryBytes, null, address);
	}

	private byte[] forwardToAddress(final byte[] queryBytes, Message query,
			final SocketAddress address) {
		if (address == null) {
			logger.warn("None of forward DNS servers are valid!");
			return null;
		}
		long timeStart = System.currentTimeMillis();
		byte[] result = null;

		FutureTask<Object> future = new FutureTask<Object>(
				new Callable<Object>() {
					public Object call() throws IOException {
						return forward0(queryBytes, address);
					}
				});
		getExecutor().execute(future);
		try {
			result = (byte[]) future.get(configure.getDnsTimeOut(),
					TimeUnit.MILLISECONDS);
			long timeCost = System.currentTimeMillis() - timeStart;
			if (logger.isTraceEnabled()) {
				logger.trace("time cost " + timeCost + "\t" + address);
			}
			dnsHostsContainer.registerTimeCost(address, timeCost);
		} catch (Exception e) {
			// when TimeoutException is thrown,the thread will suspend
			// until future.cancel() invoked.
			future.cancel(true);
			dnsHostsContainer.registerFail(address);
			if (query != null) {
				logger.warn("forward "
						+ RecordUtils.recordKey(query.getQuestion()) + " to "
						+ address + " error " + e);
			} else {
				logger.warn("forward " + " to " + address + " error " + e);
			}
		}
		return result;
	}

	private byte[] forward0(byte[] query, SocketAddress address)
			throws IOException {
		DatagramChannel dc = null;
		dc = DatagramChannel.open();
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
