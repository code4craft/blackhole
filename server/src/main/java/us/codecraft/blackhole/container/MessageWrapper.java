package us.codecraft.blackhole.container;

import org.xbill.DNS.Message;

/**
 * Wrap the message because it is hard to modify some part of the message.
 * 
 * @author yihua.huang@dianping.com
 * @date Mar 24, 2013
 */
public class MessageWrapper {

	private Message message;

	private boolean hasRecord;

	/**
	 * @param message
	 */
	public MessageWrapper(Message message) {
		if (message == null) {
			throw new IllegalArgumentException("Message should not be null!");
		}
		this.hasRecord = false;
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}

	public boolean hasRecord() {
		return hasRecord;
	}

	public void setHasRecord(boolean hasRecord) {
		this.hasRecord = hasRecord;
	}

	public void setMessage(Message message) {
		if (message == null) {
			throw new IllegalArgumentException("Message should not be null!");
		}
		this.message = message;
	}

}
