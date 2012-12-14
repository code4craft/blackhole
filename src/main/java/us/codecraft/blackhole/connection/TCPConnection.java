package us.codecraft.blackhole.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.xbill.DNS.Message;

import us.codecraft.blackhole.server.QueryProcesser;

public class TCPConnection implements Runnable {

	private static Logger log = Logger.getLogger(TCPConnection.class);

	private Socket socket;

	public TCPConnection(Socket socket) {
		super();
		this.socket = socket;
	}

	public void run() {

		try {

			try {
				int inLength;
				DataInputStream dataIn;
				DataOutputStream dataOut;
				byte[] in;

				InputStream is = socket.getInputStream();
				dataIn = new DataInputStream(is);
				inLength = dataIn.readUnsignedShort();
				in = new byte[inLength];
				dataIn.readFully(in);

				Message request;
				byte[] response = null;
				try {
					request = new Message(in);
					log.info("query");
					Message process = QueryProcesser.instance()
							.process(request);
					response = process.toWire();
					if (response == null) {
						return;
					}
				} catch (IOException e) {
				}
				dataOut = new DataOutputStream(socket.getOutputStream());
				dataOut.writeShort(response.length);
				dataOut.write(response);
			} catch (IOException e) {

				log.debug("Error sending TCP response to "
						+ socket.getRemoteSocketAddress() + ":"
						+ socket.getPort() + ", " + e);

			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}

		} catch (Throwable e) {

			log.warn("Error processing TCP connection from "
					+ socket.getRemoteSocketAddress() + ":" + socket.getPort()
					+ ", " + e);
		}
	}
}
