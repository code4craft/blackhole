package us.codecraft.blackhole.forward;

import org.xbill.DNS.Message;

import us.codecraft.blackhole.connector.UDPConnectionResponser;

/**
 * @author yihua.huang@dianping.com
 * @date Apr 10, 2013
 */
public class ForwardAnswer {

	private final Message query;

	private final UDPConnectionResponser responser;

	private final long startTime;

	/**
	 * @param responser
	 * @param startTime
	 */
	public ForwardAnswer(Message query, UDPConnectionResponser responser) {
		super();
		this.query = query;
		this.responser = responser;
		this.startTime = System.currentTimeMillis();
	}

	public UDPConnectionResponser getResponser() {
		return responser;
	}

	public long getStartTime() {
		return startTime;
	}

	public Message getQuery() {
		return query;
	}

}
