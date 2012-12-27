package us.codecraft.blackhole.forward;

import java.net.SocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Address;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 21, 2012
 */
@Component
public class DNSHostTester implements HostTester {

	private static final String DUMMY_ADDRESS = "0.0.0.0";

	private static final String DUMMY_HOST = "www.baidu.com.";

	@Autowired
	private UDPForwardConnector udpForwardConnector;

	private volatile byte[] dummyBytes;

	private byte[] getDummyBytes() {
		if (dummyBytes == null) {
			synchronized (this) {
				try {
					Message dummyQuery = Message.newQuery(new ARecord(new Name(
							DUMMY_HOST), DClass.IN, 100, Address
							.getByAddress(DUMMY_ADDRESS)));
					dummyBytes = dummyQuery.toWire();
				} catch (Exception e) {
				}
			}
		}
		return dummyBytes;

	}

	/**
	 * dns host test
	 * 
	 * @return
	 */
	@Override
	public long timeCost(SocketAddress address) {
		long timeStart = System.currentTimeMillis();
		udpForwardConnector.forwardDummy(getDummyBytes(), address);
		return System.currentTimeMillis() - timeStart;
	}
}
