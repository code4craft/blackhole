package us.codecraft.blackhole.container;

import java.net.DatagramSocket;

/**
 * Defines a set of methods that a handler uses to communicate with the main
 * MessageProcessor.
 * 
 * @author yihua.huang@dianping.com
 * @date Dec 15, 2012
 */
public class ServerContext {

	public static ThreadLocal<DatagramSocket> udpSocket = new ThreadLocal<DatagramSocket>();;

	public static ThreadLocal<Boolean> hasRecordLocal = new ThreadLocal<Boolean>();

	public static DatagramSocket getUdpSocket() {
		return udpSocket.get();
	}

	public static void setUdpSocket(DatagramSocket socket1) {
		udpSocket.set(socket1);
	}

	public static boolean hasRecord() {
		if (hasRecordLocal.get() == null) {
			return false;
		} else {
			return hasRecordLocal.get();
		}
	}

	public static void setHasRecord(boolean hasRecord) {
		hasRecordLocal.set(hasRecord);
	}
}
