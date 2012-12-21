package us.codecraft.blackhole.forward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 21, 2012
 */
@Component
public class DNSHostsContainer extends HostsContainer {

	@Autowired
	private DNSHostTester dnsHostTester;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.blackhole.forward.HostsContainer#getHostTester()
	 */
	@Override
	protected HostTester getHostTester() {
		return dnsHostTester;
	}

}
