package us.codecraft.wifesays.me;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TCPSocketMonitor extends Thread {

	private Logger log = Logger.getLogger(this.getClass());

	private InetAddress addr;
	private int port;
	private ServerSocket serverSocket;
	@Autowired
	private HusbandEar husbandEar;

	private ExecutorService executorService = Executors.newFixedThreadPool(100);

	public TCPSocketMonitor(String host, int port) throws IOException {
		super();
		this.port = port;
		this.addr = Inet4Address.getByName(host);
		serverSocket = new ServerSocket(port, 128, addr);

		this.setDaemon(true);
	}

	@Override
	public void run() {

		log.info("Starting TCP socket monitor on address "
				+ getAddressAndPort());

		while (true) {
			try {

				final Socket socket = serverSocket.accept();

				log.debug("TCP connection from "
						+ socket.getRemoteSocketAddress());

				executorService.execute(new TCPConnection(socket, husbandEar));

			} catch (SocketException e) {

				// This is usally thrown on shutdown
				log.debug("SocketException thrown from TCP socket on address "
						+ getAddressAndPort() + ", " + e);
				break;

			} catch (IOException e) {

				log.info("IOException thrown by TCP socket on address "
						+ getAddressAndPort() + ", " + e);
			}
		}
		log.info("TCP socket monitor on address " + getAddressAndPort()
				+ " shutdown");
	}

	public InetAddress getAddr() {

		return addr;
	}

	public int getPort() {

		return port;
	}

	public ServerSocket getServerSocket() {

		return serverSocket;
	}

	public void closeSocket() throws IOException {

		log.info("Closing TCP socket monitor on address " + getAddressAndPort()
				+ "...");

		this.serverSocket.close();
	}

	public String getAddressAndPort() {

		return addr.getHostAddress() + ":" + port;
	}
}
