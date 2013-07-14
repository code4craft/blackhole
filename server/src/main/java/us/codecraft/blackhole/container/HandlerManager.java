package us.codecraft.blackhole.container;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.blackhole.answer.PostAnswerHandler;
import us.codecraft.blackhole.answer.PreAnswerHandler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class HandlerManager implements InitializingBean {

	private List<Handler> preHandlers;

    private List<Handler> postHandlers;

	@Autowired
	private PreAnswerHandler preAnswerHandler;

    @Autowired
    private PostAnswerHandler postAnswerHandler;

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
		preHandlers = new LinkedList<Handler>();
		preHandlers.add(headerHandler);
		preHandlers.add(preAnswerHandler);
        postHandlers = new LinkedList<Handler>();
        postHandlers.add(postAnswerHandler);
	}

	/**
	 * @return the handlers
	 */
	public List<Handler> getPreHandlers() {
		return Collections.unmodifiableList(preHandlers);
	}

    /**
     * @return the handlers
     */
    public List<Handler> getPostHandlers() {
        return Collections.unmodifiableList(postHandlers);
    }

}
