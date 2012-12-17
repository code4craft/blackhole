package us.codecraft.blackhole.connector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;
import org.xbill.DNS.Message;

import us.codecraft.blackhole.container.MessageProcesser;
import us.codecraft.blackhole.container.ServerContext;
import us.codecraft.blackhole.utils.SpringLocator;

public class UDPConnectionWorker implements Runnable {

	private static final Logger logger = Logger
			.getLogger(UDPConnectionWorker.class);

	private final DatagramSocket socket;
	private final DatagramPacket inDataPacket;

	private MessageProcesser messageProcesser;

	public UDPConnectionWorker(DatagramSocket socket,
			DatagramPacket inDataPacket, MessageProcesser queryProcesser) {
		super();
		this.socket = socket;
		this.inDataPacket = inDataPacket;
		this.messageProcesser = queryProcesser;
	}

	public void run() {

		try {

			byte[] response = null;

			try {
				Message query = new Message(inDataPacket.getData());

				Message responseMessage = messageProcesser.process(query);
				ServerContext.setUdpSocket(socket);
				if (ServerContext.hasRecord()) {
					response = responseMessage.toWire();
				} else {
					UDPForwardConnection connection = SpringLocator
							.getBean(UDPForwardConnection.class);
					response = connection.forward(inDataPacket.getData());
					if (response == null) {
						return;
					}
				}
			} catch (IOException e) {
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
