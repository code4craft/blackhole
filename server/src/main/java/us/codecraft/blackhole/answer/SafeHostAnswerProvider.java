package us.codecraft.blackhole.answer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Type;
import us.codecraft.blackhole.antipollution.SafeHostManager;
import us.codecraft.blackhole.config.Configure;

/**
 * @author yihua.huang@dianping.com
 * @date Feb 20, 2013
 */
@Component
public class SafeHostAnswerProvider implements AnswerProvider {

	@Autowired
	private SafeHostManager safeBoxService;

	@Autowired
	private Configure configure;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.blackhole.answer.AnswerProvider#getAnswer(java.lang.String,
	 * int)
	 */
	@Override
	public String getAnswer(String query, int type) {
		if (!configure.isEnableSafeBox()) {
			return null;
		}
		if (type == Type.A || type == Type.AAAA) {
			return safeBoxService.get(StringUtils.removeEnd(query, "."));
		}
		return null;
	}

}
