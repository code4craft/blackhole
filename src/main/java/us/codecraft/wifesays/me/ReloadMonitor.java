package us.codecraft.wifesays.me;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author yihua.huang@dianping.com
 * @date 2012-12-15
 */
@Component
public class ReloadMonitor implements StandReady {
	private List<ReloadAble> reloadList;
	private ExecutorService reloadExecutors = Executors.newFixedThreadPool(10);
	private Logger logger = Logger.getLogger(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.wifesays.me.StandReady#whatYouShouldDo(java.lang.String)
	 */
	@Override
	public String doWhatYouShouldDo(String whatWifeSays) {
		if (Commands.RELOAD.equalsIgnoreCase(whatWifeSays)) {
			for (final ReloadAble reloadAble : reloadList) {
				reloadExecutors.execute(new Runnable() {

					@Override
					public void run() {
						try {
							reloadAble.reload();
						} catch (Throwable e) {
							logger.warn("oops!My ears!", e);
						}

					}
				});
			}
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
		return ReloadAble.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.StandReady#setJobs(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setJobs(List<? extends JobTodo> jobs) {
		reloadList = (List<ReloadAble>) jobs;
	}

}
