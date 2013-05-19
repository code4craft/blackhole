package us.codecraft.blackhole.container;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.blackhole.answer.AnswerHandler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class HandlerManager implements InitializingBean {

	private List<Handler> handlers;

	@Autowired
	private AnswerHandler answerHandler;

	@Autowired
	private HeaderHandler headerHandler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		registerHandlers();
	}

	public void registerHandlers() {
		handlers = new LinkedList<Handler>();
		handlers.add(headerHandler);
		handlers.add(answerHandler);
	}

	/**
	 * @return the handlers
	 */
	public List<Handler> getHandlers() {
		return Collections.unmodifiableList(handlers);
	}

}
