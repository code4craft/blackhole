package us.codecraft.blackhole.connector;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import us.codecraft.blackhole.container.QueryProcesser;

/**
 * Authored by EagleDNS<a href="http://www.unlogic.se/projects/eagledns">
 * http://www.unlogic.se/projects/eagledns</a>
 * 
 * @author yihua.huang@dianping.com
 * @date 2012-12-17
 */
public class UDPSocketMonitor extends Thread {

	private Logger logger = Logger.getLogger(this.getClass());

	private InetAddress addr;
	private int port;
	private ExecutorService executorService = Executors.newFixedThreadPool(50);
	@Autowired
	private QueryProcesser queryProcesser;

	public UDPSocketMonitor(String host, int port) throws IOException {
		super();
		this.addr = Inet4Address.getByName(host);
		this.port = port;

		this.setDaemon(true);
	}

	@Override
	public void run() {

		logger.info("Starting UDP socket monitor on address "
				+ this.getAddressAndPort());

		while (true) {
			try {

				Selector selector = Selector.open();
				ByteBuffer byteBuffer = ByteBuffer.allocate(512);
				DatagramChannel datagramChannel = DatagramChannel.open();
				InetSocketAddress address = new InetSocketAddress(addr, port);
				datagramChannel.socket().bind(address);
				datagramChannel.configureBlocking(false);
				SelectionKey selectionKey = datagramChannel.register(selector,
						SelectionKey.OP_READ);

				while (true) // 不断的轮询
				{
					selector.select();
					Iterator<SelectionKey> selectionKeyIterator = selector
							.selectedKeys().iterator();
					while (selectionKeyIterator.hasNext()) {
						selectionKey = (SelectionKey) selectionKeyIterator
								.next();
						selectionKeyIterator.remove();
						processSelectionKey(selector, selectionKey, byteBuffer);
					}
				}

			} catch (SocketException e) {

				// This is usally thrown on shutdown
				logger.debug(
						"SocketException thrown from UDP socket on address "
								+ this.getAddressAndPort() + ", ", e);
				break;
			} catch (IOException e) {

				logger.info("IOException thrown by UDP socket on address "
						+ this.getAddressAndPort() + ", " + e);
			}
		}
		logger.info("UDP socket monitor on address " + getAddressAndPort()
				+ " shutdown");
	}

	private void processSelectionKey(Selector selector,
			SelectionKey selectionKey, ByteBuffer byteBuffer)
			throws IOException {
		if (selectionKey.isReadable()) {
			DatagramChannel datagramChannel = (DatagramChannel) selectionKey
					.channel();
			byteBuffer.clear();
			SocketAddress clientAddress = datagramChannel.receive(byteBuffer);
			byte[] copyOfRange = Arrays.copyOfRange(byteBuffer.array(), 0,
					byteBuffer.remaining());
			executorService.execute(new UDPConnectionWorker(copyOfRange,
					clientAddress, selector, queryProcesser));
		} else if (selectionKey.isWritable()) {
			DatagramChannel datagramChannel = (DatagramChannel) selectionKey
					.channel();
			byte[] response = (byte[]) selectionKey.attachment();
			byteBuffer.clear();
			byteBuffer.put(response);
			byteBuffer.flip();
			datagramChannel.write(byteBuffer);
		}
	}

	public String getAddressAndPort() {

		return addr.getHostAddress() + ":" + port;
	}
}
