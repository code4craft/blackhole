package us.codecraft.blackhole.answer;

/**
 * Provide the answer.An answerContainer must be registered in
 * {@link PreAnswerHandler#regitestProviders()} before it takes effect.
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
