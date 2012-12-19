package us.codecraft.blackhole.connector;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

import org.apache.log4j.Logger;

import us.codecraft.blackhole.container.QueryProcesser;

public class UDPConnectionWorker implements Runnable {

	private static final Logger logger = Logger
			.getLogger(UDPConnectionWorker.class);

	private final byte[] query;
	private final SocketAddress clientAddress;
	private final Selector selector;
	public static final short DEFAULT_UDP_LENGTH = 512;

	private QueryProcesser queryProcesser;

	/**
	 * @param query
	 * @param clientAddress
	 * @param selector
	 * @param queryProcesser
	 */
	public UDPConnectionWorker(byte[] query, SocketAddress clientAddress,
			Selector selector, QueryProcesser queryProcesser) {
		super();
		this.query = query;
		this.clientAddress = clientAddress;
		this.selector = selector;
		this.queryProcesser = queryProcesser;
	}

	@SuppressWarnings("unused")
	private <T> String printArray(byte[] copyOfRange) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (byte t : copyOfRange) {
			builder.append(String.valueOf(t));
			builder.append(",");
		}
		builder.append("]");
		return builder.toString();
	}

	public void run() {

		byte[] response = null;
		try {
			ByteBuffer byteBuffer = ByteBuffer.allocate(DEFAULT_UDP_LENGTH);
			response = queryProcesser.process(query);
			DatagramChannel datagramChannel1 = DatagramChannel.open();
			byteBuffer.clear();
			byteBuffer.put(response);
			byteBuffer.flip();
			datagramChannel1.configureBlocking(false);
			datagramChannel1.send(byteBuffer, clientAddress);
		} catch (Throwable e) {
			logger.warn("worker exception", e);
		}
	}
}
