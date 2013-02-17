package us.codecraft.blackhole.forward.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yihua.huang@dianping.com
 * @date Feb 17, 2013
 */
public class ForwardAnswer {

	private byte[] answer;

	private final ReentrantLock lock = new ReentrantLock();

	private final Condition condition = lock.newCondition();

	public ForwardAnswer() {

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

}
