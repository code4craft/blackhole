package us.codecraft.blackhole.zones;

import us.codecraft.blackhole.container.AnswerHandler;

/**
 * Provide the answer.An answerProvider must be registered in
 * {@link AnswerHandler#regitestProviders()} before it takes effect.
 * 
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
public interface AnswerProvider {

	/**
	 * 
	 * @param query
	 * @param type
	 * @return
	 */
	public String getAnswer(String query, int type);

}
