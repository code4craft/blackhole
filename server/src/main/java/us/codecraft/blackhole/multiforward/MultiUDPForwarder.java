package us.codecraft.blackhole.multiforward;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;

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

	private Random random = new Random();

	private final static int PORT_BASE = 40311;

	@Autowired
	private DNSHostsContainer dnsHostsContainer;

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
		// send to all address
		ByteBuffer byteBuffer = ByteBuffer.allocate(512);
		ForwardAnswer forwardAnswer = new ForwardAnswer(query,
				new HashSet<SocketAddress>(allAvaliableHosts));
		try {
			DatagramChannel datagramChannel = DatagramChannel.open();
			bindToPort(datagramChannel);
			multiUDPReceiver.registerReceiver(datagramChannel, forwardAnswer);
			for (SocketAddress host : allAvaliableHosts) {
				byteBuffer.clear();
				byteBuffer.put(queryBytes);
				byteBuffer.flip();
				datagramChannel.send(byteBuffer, host);
				logger.info("forward to " + host);
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
			logger.warn("timeout for all");
		}
		return forwardAnswer.getAnswer();
	}

	private int bindToPort(DatagramChannel datagramChannel) {
		boolean portAvaliable = true;
		int port = 0;
		do {
			try {
				// todo some algorithm to reduce the retry time
				port = PORT_BASE + random.nextInt(1000);
				datagramChannel.bind(new InetSocketAddress(port));
				portAvaliable = true;
			} catch (Exception e) {
				portAvaliable = false;
			}
		} while (!portAvaliable);
		return port;
	}

}
