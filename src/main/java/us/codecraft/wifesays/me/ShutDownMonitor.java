package us.codecraft.wifesays.me;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import us.codecraft.blackhole.ExecutorUtils;

/**
 * @author yihua.huang@dianping.com
 * @date 2012-12-15
 */
@Component
public class ShutDownMonitor implements StandReady {

	private List<ShutDownAble> shutDownList;

	private Logger logger = Logger.getLogger(getClass());

	private ExecutorService shutDownExecutors = ExecutorUtils
			.newBlockingExecutors(4);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.wifesays.me.StandReady#whatYouShouldDo(java.lang.String)
	 */
	@Override
	public String doWhatYouShouldDo(String whatWifeSays) {
		if (Commands.SHUTDOWN.equalsIgnoreCase(whatWifeSays)) {
			for (final ShutDownAble shutDownAble : shutDownList) {
				shutDownExecutors.execute(new Runnable() {

					@Override
					public void run() {
						try {
							shutDownAble.shutDown();
						} catch (Throwable e) {
							logger.warn("oops!My ears!", e);
						}
					}
				});
			}
			logger.info("shutdown success");
			return "success";
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.StandReady#whatWillYouDo()
	 */
	@Override
	public Class<? extends JobTodo> whatKindOfJobWillYouDo() {
		return ShutDownAble.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.StandReady#setJobs(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setJobs(List<? extends JobTodo> jobs) {
		shutDownList = (List<ShutDownAble>) jobs;
	}

}
