package us.codecraft.wifesays.me;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import org.apache.log4j.Logger;

public class TCPConnection implements Runnable {

	private static Logger log = Logger.getLogger(TCPConnection.class);

	private Socket socket;

	private TextProcessor textProcessor;

	public TCPConnection(Socket socket, TextProcessor textProcessor) {
		super();
		this.socket = socket;
		this.textProcessor = textProcessor;
	}

	public void run() {

		try {

			try {
				PrintStream out = new PrintStream(socket.getOutputStream());
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					line = line.replaceAll("^\\s+", "").replaceAll("\\s+", "");
					String process = textProcessor.process(line);
					out.println(process);
				}
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

			log.warn(
					"Error processing TCP connection from "
							+ socket.getRemoteSocketAddress() + ":"
							+ socket.getPort() + ", ", e);
		}
	}
}
