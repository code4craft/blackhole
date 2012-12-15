package us.codecraft.wifesays.me;

import java.util.List;

/**
 * When you are doing something, you need hear what your wife say!
 * 
 * @author yihua.huang@dianping.com
 * @date 2012-12-14
 */
public interface StandReady {

	/**
	 * when wife says, what you should do?
	 * 
	 * @param whatWifeSays
	 */
	public String doWhatYouShouldDo(String whatWifeSays);

	/**
	 * Which kind of job you will do after hear what wife says?
	 * 
	 * @return
	 */
	public Class<? extends JobTodo> whatKindOfJobWillYouDo();

	/**
	 * Assign jobs to you!
	 * 
	 * @param jobs
	 */
	public void setJobs(List<? extends JobTodo> jobs);

}
