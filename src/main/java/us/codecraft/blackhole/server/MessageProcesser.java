package us.codecraft.blackhole.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class MessageProcesser {

	@Autowired
	private HandlerManager handlerManager;

	public Message process(Message request) {
		Message response = new Message(request.getHeader().getID());
		for (Handler handler : handlerManager.getHandlers()) {
			boolean handle = handler.handle(request, response);
			if (!handle) {
				break;
			}
		}
		return response;
	}

}
