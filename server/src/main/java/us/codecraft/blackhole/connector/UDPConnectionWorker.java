package us.codecraft.blackhole.connector;

import java.net.DatagramPacket;

import org.apache.log4j.Logger;
import org.xbill.DNS.Message;

import us.codecraft.blackhole.container.QueryProcesser;
import us.codecraft.blackhole.context.RequestContextProcessor;
import us.codecraft.blackhole.forward.Forwarder;

public class UDPConnectionWorker implements Runnable {

	private static final Logger logger = Logger
			.getLogger(UDPConnectionWorker.class);

	private final UDPConnectionResponser responser;
	private final DatagramPacket inDataPacket;

	private QueryProcesser queryProcesser;
	private Forwarder forwarder;

	public UDPConnectionWorker(DatagramPacket inDataPacket,
			QueryProcesser queryProcesser, UDPConnectionResponser responser,
			Forwarder forwarder) {
		super();
		this.responser = responser;
		this.inDataPacket = inDataPacket;
		this.queryProcesser = queryProcesser;
		this.forwarder = forwarder;
	}

	public void run() {

		try {

            RequestContextProcessor.processRequest(inDataPacket);
			byte[] response = null;
			response = queryProcesser.process(inDataPacket.getData());
			if (response != null) {
				responser.response(response);
			} else {
				forwarder.forward(inDataPacket.getData(), new Message(
						inDataPacket.getData()), responser);
			}
		} catch (Throwable e) {

			logger.warn(
					"Error processing UDP connection from "
							+ inDataPacket.getSocketAddress() + ", ", e);
		}
	}
}
