package us.codecraft.blackhole.forward;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;
import org.xbill.DNS.Type;

import us.codecraft.blackhole.config.Configure;

/**
 * Forward DNS query to hosts contained in {@link DNSHostsContainer}.Use the
 * same port 40311 for all UDP diagram and the instance of
 * {@link MultiUDPReceiver} will listen on the port 40311.Use wait/notify to
 * synchronize.
 * 
 * @author yihua.huang@dianping.com
 * @date Jan 16, 2013
 */
@Component
public class MultiUDPForwarder implements Forwarder {

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
		// get address
		List<SocketAddress> allAvaliableHosts = dnsHostsContainer
				.getAllAvaliableHosts();
		return forward(queryBytes, query, allAvaliableHosts);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.blackhole.forward.Forwarder#forward(byte[],
	 * org.xbill.DNS.Message, java.util.List)
	 */
	@Override
	public byte[] forward(byte[] queryBytes, Message query,
			List<SocketAddress> hosts) {
		if (logger.isDebugEnabled()) {
			logger.debug("forward query " + query);
		}
		// send to all address
		ByteBuffer byteBuffer = ByteBuffer.allocate(512);
		ForwardAnswer forwardAnswer = new ForwardAnswer(query,
				new HashSet<SocketAddress>(hosts));
		// send fake dns query to detect dns poisoning
		hosts.add(0, configure.getFakeDnsServer());
		multiUDPReceiver.registerReceiver(query.getHeader().getID(),
				forwardAnswer);
		try {
			DatagramChannel datagramChannel = multiUDPReceiver
					.getDatagramChannel();
			for (SocketAddress host : hosts) {
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
		long time1 = System.currentTimeMillis();
		if (forwardAnswer.getAnswer() == null) {
			try {
				forwardAnswer.getLock().lockInterruptibly();
				forwardAnswer.getCondition().await(configure.getDnsTimeOut(),
						TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				logger.warn("error", e);
			} finally {
				forwardAnswer.getLock().unlock();
			}
		}
		if (forwardAnswer.getAnswer() == null) {
			logger.warn("timeout for query "
					+ query.getQuestion().getName().toString() + " "
					+ Type.string(query.getQuestion().getType())
					+ " time cost " + (System.currentTimeMillis() - time1));
		}
		return forwardAnswer.getAnswer();
	}

}
