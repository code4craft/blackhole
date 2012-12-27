package us.codecraft.blackhole.handler;

import org.xbill.DNS.Message;

/**
 * 
 * Handlers process the request and generate the response.<br/>
 * If there are more than one handler, they are processed as a chain.
 * 
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
public interface Handler {

	/**
	 * Process the request and generate the response.<br/>
	 * If there are more than one handler, they are processed as a chain.<br/>
	 * Unlike java servlet-api, the request is completely constructed before
	 * handling,and the response will be sent to client only if all handlers
	 * process completed.
	 * 
	 * @param request
	 *            message from client
	 * @param response
	 *            message to client
	 * @return if the handler need end the entire handle process, it should
	 *         return true,otherwise it should return false and pass the context
	 *         to next handler
	 */
	public boolean handle(Message request, Message response);

}
