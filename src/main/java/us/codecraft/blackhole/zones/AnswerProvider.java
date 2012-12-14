package us.codecraft.blackhole.zones;

/**
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
