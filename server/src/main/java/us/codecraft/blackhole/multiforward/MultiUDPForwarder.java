package us.codecraft.blackhole.multiforward;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;

import us.codecraft.blackhole.config.Configure;
import us.codecraft.blackhole.forward.DNSHostsContainer;
import us.codecraft.blackhole.forward.Forwarder;

/**
 * @author yihua.huang@dianping.com
 * @date Jan 16, 2013
 */
@Qualifier("multiUDPForwarderConnector")
@Component
public class MultiUDPForwarder implements Forwarder {

	private int timeout = 3000;

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private MultiUDPReceiver multiUDPReceiver;

	@Autowired
	private DNSHostsContainer dnsHostsContainer;

	@Autowired
	private Configure configure;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.blackhole.forward.Forwarder#forward(byte[],
	 * org.xbill.DNS.Message)
	 */
	@Override
	public byte[] forward(byte[] queryBytes, Message query) {
		if (logger.isDebugEnabled()) {
			logger.debug("forward query " + query);
		}
		// get address
		List<SocketAddress> allAvaliableHosts = dnsHostsContainer
				.getAllAvaliableHosts();
		// send to all address
		ByteBuffer byteBuffer = ByteBuffer.allocate(512);
		ForwardAnswer forwardAnswer = new ForwardAnswer(query,
				new HashSet<SocketAddress>(allAvaliableHosts));
		// send fake dns query to detect dns poisoning
		allAvaliableHosts.add(0, configure.getFakeDnsServer());
		multiUDPReceiver.registerReceiver(query.getHeader().getID(),
				forwardAnswer);
		try {
			DatagramChannel datagramChannel = multiUDPReceiver
					.getDatagramChannel();
			for (SocketAddress host : allAvaliableHosts) {
				byteBuffer.clear();
				byteBuffer.put(queryBytes);
				byteBuffer.flip();
				datagramChannel.send(byteBuffer, host);
				if (logger.isDebugEnabled()) {
					logger.debug("forward to " + host);
				}
			}
		} catch (IOException e) {
			logger.warn("error", e);
		}

		if (forwardAnswer.getAnswer() == null) {
			try {
				forwardAnswer.getLock().lockInterruptibly();
				forwardAnswer.getCondition().await(timeout,
						TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				logger.warn("error", e);
			} finally {
				forwardAnswer.getLock().unlock();
			}
		}
		if (forwardAnswer.getAnswer() == null) {
			logger.warn("timeout for query "
					+ query.getQuestion().getName().toString());
		}
		return forwardAnswer.getAnswer();
	}

}
