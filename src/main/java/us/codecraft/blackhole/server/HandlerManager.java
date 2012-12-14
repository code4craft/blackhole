package us.codecraft.blackhole.server;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
public class HandlerManager {

	private List<Handler> handlers;

	private HandlerManager() {
		handlers = new LinkedList<Handler>();
		handlers.add(new HeaderHandler());
		handlers.add(new AnswerHandler());
	}

	private static volatile HandlerManager INSTANCE;

	public static HandlerManager instance() {
		if (INSTANCE == null) {
			synchronized (HandlerManager.class) {
				INSTANCE = new HandlerManager();
			}
		}
		return INSTANCE;
	}

	/**
	 * @return the handlers
	 */
	public List<Handler> getHandlers() {
		return Collections.unmodifiableList(handlers);
	}

}
