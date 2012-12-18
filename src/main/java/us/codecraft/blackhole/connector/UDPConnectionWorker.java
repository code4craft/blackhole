package us.codecraft.blackhole.connector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

import us.codecraft.blackhole.container.QueryProcesser;
import us.codecraft.blackhole.container.ServerContext;

public class UDPConnectionWorker implements Runnable {

	private static final Logger logger = Logger
			.getLogger(UDPConnectionWorker.class);

	private final DatagramSocket socket;
	private final DatagramPacket inDataPacket;

	private QueryProcesser queryProcesser;

	public UDPConnectionWorker(DatagramSocket socket,
			DatagramPacket inDataPacket, QueryProcesser queryProcesser) {
		super();
		this.socket = socket;
		this.inDataPacket = inDataPacket;
		this.queryProcesser = queryProcesser;
	}

	public void run() {

		try {

			byte[] response = null;
			ServerContext.setUdpSocket(socket);
			response = queryProcesser.process(inDataPacket.getData());
			if (response == null) {
				return;
			}
			DatagramPacket outdp = new DatagramPacket(response,
					response.length, inDataPacket.getAddress(),
					inDataPacket.getPort());

			outdp.setData(response);
			outdp.setLength(response.length);
			outdp.setAddress(inDataPacket.getAddress());
			outdp.setPort(inDataPacket.getPort());

			try {
				socket.send(outdp);

			} catch (IOException e) {

				logger.debug("Error sending UDP response to "
						+ inDataPacket.getAddress() + ", " + e);
			}

		} catch (Throwable e) {

			logger.warn(
					"Error processing UDP connection from "
							+ inDataPacket.getSocketAddress() + ", ", e);
		}
	}
}
