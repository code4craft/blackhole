package us.codecraft.blackhole.container;


/**
 * 
 * Handlers process the request and generate the response.<br/>
 * If there are more than one handler, they are processed as a chain.A handler
 * must be registered in {@link HandlerManager#registerHandlers()} before it
 * takes effect.
 * 
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
public interface Handler {

	/**
	 * Process the request and generate the response.<br/>
	 * If there are more than one handler, they are processed as a chain.<br/>
	 * Unlike java servlet-api, the request is completely constructed before
	 * handling(not a stream),and the response will be sent to client only if
	 * all handlers process completed.
	 * 
	 * @param request
	 *            message from client
	 * @param response
	 *            message to client
	 * @return true: pass the request to next handler<br>
	 *         false: finish the entire handle process.
	 */
	public boolean handle(MessageWrapper request, MessageWrapper response);

}
