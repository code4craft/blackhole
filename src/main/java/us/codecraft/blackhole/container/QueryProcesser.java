package us.codecraft.blackhole.container;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;

import us.codecraft.blackhole.connector.UDPForwardConnection;
import us.codecraft.blackhole.handler.Handler;
import us.codecraft.blackhole.handler.HandlerManager;

/**
 * Main logic of blackhole.<br/>
 * Process the DNS query and return the answer.
 * 
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class QueryProcesser {

	@Autowired
	private HandlerManager handlerManager;

	@Autowired
	private UDPForwardConnection forwardConnection;

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(getClass());

	public byte[] process(byte[] queryData) throws IOException {

		byte[] response = null;
		Message query = new Message(queryData);
		Message responseMessage = new Message(query.getHeader().getID());
		for (Handler handler : handlerManager.getHandlers()) {
			boolean handle = handler.handle(query, responseMessage);
			if (!handle) {
				break;
			}
		}
		if (ServerContext.hasRecord()) {
			response = responseMessage.toWire();
		} else {
			response = forwardConnection.forward(queryData);
			if (response == null) {
				return null;
			}
		}

		return response;
	}

}
