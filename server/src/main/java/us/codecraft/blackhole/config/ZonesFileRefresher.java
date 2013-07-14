package us.codecraft.blackhole.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.blackhole.answer.CustomAnswerPatternProvider;
import us.codecraft.blackhole.cache.CacheManager;
import us.codecraft.blackhole.utils.RecordUtils;
import us.codecraft.wifesays.me.StandReadyWorker;

import java.io.File;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 29, 2012
 */
@Component
public class ZonesFileRefresher extends StandReadyWorker implements InitializingBean {

    @Autowired
    private Configure configure;

    @Autowired
    private ZonesFileLoader zonesFileLoader;

    @Autowired
    private CustomAnswerPatternProvider customAnswerPatternProvider;

    private ScheduledExecutorService scheduledExecutorService = Executors
            .newScheduledThreadPool(1);

    private long lastFileModifiedTime;

    //delete_zones_ip_192.168.0.1
    private static final String DELETE_ZONES_IP = "delete_zones_ip_";
    //add_zones_ip_192.168.0.1:127.0.0.1 *.dianping.com
    private static final String ADD_ZONES_IP = "add_zones_ip_";

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        File zonesFile = new File(Configure.getZonesFilename());
        lastFileModifiedTime = zonesFile.lastModified();

        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {

            public void run() {
                File zonesFile = new File(Configure.getZonesFilename());
                // When two files' last modify time not equal, we consider it is
                // changed.
                synchronized (this) {
                    if (zonesFile.lastModified() != lastFileModifiedTime) {
                        lastFileModifiedTime = zonesFile.lastModified();
                        zonesFileLoader.reload();
                    }
                }
            }
        }, 500, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public String doWhatYouShouldDo(String whatWifeSays) {
        if (StringUtils.startsWithIgnoreCase(whatWifeSays, ADD_ZONES_IP)) {
            String line = StringUtils.removeStart(whatWifeSays, ADD_ZONES_IP);
            try {
                ZonesPattern zonesPattern = ZonesPattern.parse(line);
                if (zonesPattern == null) {
                    return "PARSE ERROR";
                }
                for (Pattern pattern : zonesPattern.getPatterns()) {
                    customAnswerPatternProvider.getPatterns().put(zonesPattern.getUserIp(), pattern, zonesPattern.getTargetIp());
                }
                return "SUCCESS, " + zonesPattern.getPatterns().size() + " patterns added.";
            } catch (UnknownHostException e) {
                return "ERROR " + e;
            }
        } else if (StringUtils.startsWithIgnoreCase(whatWifeSays, DELETE_ZONES_IP)) {
            String ip = StringUtils.removeStart(whatWifeSays, DELETE_ZONES_IP);
            if (RecordUtils.isValidIpv4Address(ip)) {
                customAnswerPatternProvider.getPatterns().remove(ip);
                return "REMOVE SUCCESS";
            } else {
                return "ERROR, invalid ip " + ip;
            }
        }

        return null;
    }
}
