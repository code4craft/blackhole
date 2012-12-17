package us.codecraft.blackhole.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;
import org.xbill.DNS.Message;

import us.codecraft.blackhole.server.MessageProcesser;
import us.codecraft.blackhole.server.ServerContext;
import us.codecraft.blackhole.utils.SpringLocator;

public class UDPConnection implements Runnable {

	private static final Logger log = Logger.getLogger(UDPConnection.class);

	private final DatagramSocket socket;
	private final DatagramPacket inDataPacket;

	private MessageProcesser messageProcesser;

	public UDPConnection(DatagramSocket socket, DatagramPacket inDataPacket,
			MessageProcesser queryProcesser) {
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
				if (!ServerContext.hasRecord()) {
					UDPForwardConnection connection = SpringLocator
							.getBean(UDPForwardConnection.class);
					response = connection.forward(inDataPacket.getData());
					if (response == null) {
						return;
					}
				} else {
					response = responseMessage.toWire();
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

				log.debug("Error sending UDP response to "
						+ inDataPacket.getAddress() + ", " + e);
			}

		} catch (Throwable e) {

			log.warn(
					"Error processing UDP connection from "
							+ inDataPacket.getSocketAddress() + ", ", e);
		}
	}
}
