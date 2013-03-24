package us.codecraft.blackhole.container;

import java.net.SocketAddress;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;

import us.codecraft.blackhole.cache.CacheManager;
import us.codecraft.blackhole.forward.MultiUDPForwarder;
import us.codecraft.blackhole.zones.NSPatternContainer;

/**
 * @author yihua.huang@dianping.com
 * @date Mar 24, 2013
 */
@Component
public class NSForwardHandler implements Handler {

	@Autowired
	private NSPatternContainer nsPatternContainer;

	@Autowired
	private MultiUDPForwarder multiUDPForwarder;

	@Autowired
	private CacheManager cacheManager;

	private Log loggger = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.blackhole.container.Handler#handle(org.xbill.DNS.Message,
	 * org.xbill.DNS.Message)
	 */
	@Override
	public boolean handle(MessageWrapper request, MessageWrapper response) {
		String question = request.getMessage().getQuestion().getName()
				.toString();
		List<SocketAddress> nsHosts = nsPatternContainer.getNSHosts(question);
		if (nsHosts != null) {
			byte[] forwardAnswer = multiUDPForwarder.forward(request
					.getMessage().toWire(), request.getMessage(), nsHosts);
			try {
				response.setMessage(new Message(forwardAnswer));
				if (forwardAnswer != null) {
					cacheManager
							.setToCache(request.getMessage(), forwardAnswer);
				}
				return false;
			} catch (Exception e) {
				loggger.warn("parse response error" + e);
			}
		}
		return true;
	}
}
