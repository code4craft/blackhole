package us.codecraft.blackhole.multiforward;

import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.xbill.DNS.Message;

/**
 * @author yihua.huang@dianping.com
 * @date Feb 17, 2013
 */
public class ForwardAnswer {

	private byte[] answer;

	private final Message query;

	private final ReentrantLock lock = new ReentrantLock();

	private final Condition condition = lock.newCondition();

	private final Set<SocketAddress> hostSet;

	public ForwardAnswer(Message query, Set<SocketAddress> hostSet) {
		this.query = query;
		this.hostSet = hostSet;
	}

	public byte[] getAnswer() {
		return answer;
	}

	public void setAnswer(byte[] answer) {
		this.answer = answer;
	}

	public ReentrantLock getLock() {
		return lock;
	}

	public Condition getCondition() {
		return condition;
	}

	public Message getQuery() {
		return query;
	}

	public Set<SocketAddress> getHostSet() {
		return hostSet;
	}

}
