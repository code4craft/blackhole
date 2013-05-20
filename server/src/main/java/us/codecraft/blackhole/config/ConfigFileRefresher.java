package us.codecraft.blackhole.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 29, 2012
 */
@Component
public class ConfigFileRefresher implements InitializingBean {

    @Autowired
    private ConfigFileLoader configFileLoader;

    private ScheduledExecutorService scheduledExecutorService = Executors
            .newScheduledThreadPool(1);

    private long lastFileModifiedTime;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        File configFile = new File(Configure.getConfigFilename());
        lastFileModifiedTime = configFile.lastModified();

        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {

            public void run() {
                File configFile = new File(Configure.getConfigFilename());
                // When two files' last modify time not equal, we consider it is
                // changed.
                synchronized (this) {
                    if (configFile.lastModified() != lastFileModifiedTime) {
                        lastFileModifiedTime = configFile.lastModified();
                        configFileLoader.reload();
                    }
                }
            }
        }, 500, 500, TimeUnit.MILLISECONDS);
    }

}
