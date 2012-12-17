package us.codecraft.blackhole.connector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import us.codecraft.blackhole.container.MessageProcesser;

/**
 * Authored by EagleDNS<a href="http://www.unlogic.se/projects/eagledns">
 * http://www.unlogic.se/projects/eagledns</a>
 * 
 * @author yihua.huang@dianping.com
 * @date 2012-12-17
 */
public class UDPSocketMonitor extends Thread {

	private Logger log = Logger.getLogger(this.getClass());

	private InetAddress addr;
	private int port;
	private static final short udpLength = 512;
	private DatagramSocket socket;
	private ExecutorService executorService = Executors.newFixedThreadPool(100);
	@Autowired
	private MessageProcesser queryProcesser;

	public UDPSocketMonitor(String host, int port) throws IOException {
		super();
		this.addr = Inet4Address.getByName(host);
		this.port = port;

		socket = new DatagramSocket(port, addr);

		this.setDaemon(true);
	}

	@Override
	public void run() {

		log.info("Starting UDP socket monitor on address "
				+ this.getAddressAndPort());

		while (true) {
			try {

				byte[] in = new byte[udpLength];
				DatagramPacket indp = new DatagramPacket(in, in.length);

				indp.setLength(in.length);
				socket.receive(indp);
				executorService.execute(new UDPConnectionWorker(socket, indp,
						queryProcesser));
			} catch (SocketException e) {

				// This is usally thrown on shutdown
				log.debug("SocketException thrown from UDP socket on address "
						+ this.getAddressAndPort() + ", " + e);
				break;
			} catch (IOException e) {

				log.info("IOException thrown by UDP socket on address "
						+ this.getAddressAndPort() + ", " + e);
			}
		}
		log.info("UDP socket monitor on address " + getAddressAndPort()
				+ " shutdown");
	}

	public void closeSocket() throws IOException {

		log.info("Closing TCP socket monitor on address " + getAddressAndPort()
				+ "...");

		this.socket.close();
	}

	public String getAddressAndPort() {

		return addr.getHostAddress() + ":" + port;
	}
}
