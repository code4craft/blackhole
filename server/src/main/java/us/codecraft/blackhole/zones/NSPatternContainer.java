package us.codecraft.blackhole.zones;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.codecraft.blackhole.config.Configure;
import us.codecraft.blackhole.forward.DNSHostsContainer;

/**
 * Read the config to patterns and process the request record.
 * 
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class NSPatternContainer {

	private volatile Map<Pattern, String> patterns;

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private DNSHostsContainer dnsHostsContainer;

	public List<SocketAddress> getNSHosts(String query) {
		for (Entry<Pattern, String> entry : patterns.entrySet()) {
			Matcher matcher = entry.getKey().matcher(query);
			if (matcher.find()) {
				if (StringUtils.isBlank(entry.getValue())) {
					return dnsHostsContainer.getAllAvaliableHosts();
				}
				String[] hostNames = entry.getValue().split(",");
				List<SocketAddress> hosts = new ArrayList<SocketAddress>();
				for (String hostName : hostNames) {
					hosts.add(new InetSocketAddress(hostName,
							Configure.DNS_PORT));
				}
				return hosts;
			}
		}
		return null;
	}

	/**
	 * @param patterns
	 *            the patterns to set
	 */
	public void setPatterns(Map<Pattern, String> patterns) {
		this.patterns = patterns;
	}

}
