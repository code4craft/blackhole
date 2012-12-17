package us.codecraft.blackhole.container;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;

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
public class MessageProcesser {

	@Autowired
	private HandlerManager handlerManager;

	public Message process(Message query) {
		Message response = new Message(query.getHeader().getID());
		for (Handler handler : handlerManager.getHandlers()) {
			boolean handle = handler.handle(query, response);
			if (!handle) {
				break;
			}
		}
		return response;
	}

}
