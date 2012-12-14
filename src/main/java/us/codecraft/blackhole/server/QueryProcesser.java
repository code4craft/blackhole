package us.codecraft.blackhole.server;

import org.xbill.DNS.Message;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
public class QueryProcesser {

	private static volatile QueryProcesser INSTANCE;
	private HandlerManager handlerManager;

	private QueryProcesser() {
		handlerManager = HandlerManager.instance();
	}

	public static QueryProcesser instance() {
		if (INSTANCE == null) {
			synchronized (QueryProcesser.class) {
				INSTANCE = new QueryProcesser();
			}
		}
		return INSTANCE;
	}

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
