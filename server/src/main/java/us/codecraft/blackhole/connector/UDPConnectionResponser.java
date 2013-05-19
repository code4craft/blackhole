package us.codecraft.blackhole.connector;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPConnectionResponser {

	private static final Logger logger = Logger
			.getLogger(UDPConnectionResponser.class);

	private final DatagramSocket socket;
	private final DatagramPacket inDataPacket;

	public UDPConnectionResponser(DatagramSocket socket,
			DatagramPacket inDataPacket) {
		super();
		this.socket = socket;
		this.inDataPacket = inDataPacket;
	}

	public DatagramPacket getInDataPacket() {
		return inDataPacket;
	}

	public void response(byte[] response) {

		try {

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
