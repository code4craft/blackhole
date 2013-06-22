package us.codecraft.blackhole.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.blackhole.concurrent.ThreadPools;
import us.codecraft.blackhole.forward.DNSHostsContainer;
import us.codecraft.wifesays.me.ReloadAble;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetSocketAddress;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 28, 2012
 */
@Component
public class ConfigFileLoader implements InitializingBean, ReloadAble {

    @Autowired
    private DNSHostsContainer dnsHostsContainer;

    @Autowired
    private Configure configure;

    @Autowired
    private ThreadPools threadPools;

    private boolean reloadOff = false;

    /**
     * @param reloadOff the reloadOff to set
     */
    public void setReloadOff(boolean reloadOff) {
        this.reloadOff = reloadOff;
    }

    private Logger logger = Logger.getLogger(getClass());

    public void readConfig(String filename) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    filename));
            String line = null;
            dnsHostsContainer.clearHosts();
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    continue;
                }
                try {
                    String[] items = line.split("=");
                    if (items.length < 2) {
                        continue;
                    }
                    String key = items[0];
                    String value = items[1];
                    boolean configed = config(key, value);
                    if (configed) {
                        logger.info("read config success:\t" + line);
                    }
                } catch (Exception e) {
                    logger.warn("parse config line error:\t" + line, e);
                }
            }
            bufferedReader.close();
        } catch (Throwable e) {
            logger.warn("read config file failed:" + filename, e);
        }
    }

    private boolean config(String key, String value) {
        if (key.equalsIgnoreCase("ttl")) {
            configure.setTtl(Integer.parseInt(value));
        } else if (key.equalsIgnoreCase("dns")) {
            dnsHostsContainer.addHost(new InetSocketAddress(value,
                    Configure.DNS_PORT));
        } else if (key.equalsIgnoreCase("cache")) {
            configure.setUseCache(BooleanUtils.toBooleanObject(value));
        } else if (key.equalsIgnoreCase("log")) {
            configLogLevel(value);
        } else if (key.equalsIgnoreCase("dns_timeout")) {
            int dnsTimeout = Integer.parseInt(value);
            configure.setDnsTimeOut(dnsTimeout);
            dnsHostsContainer.setTimeout(dnsTimeout);
        } else if (key.equalsIgnoreCase("fake_dns")) {
            configure.setFakeDnsServer(value);
        } else if (key.equalsIgnoreCase("safe_box")) {
            configure.setEnableSafeBox(BooleanUtils.toBooleanObject(value));
        } else if (key.equalsIgnoreCase("thread_num")) {
            configure.setThreadNum(Integer.parseInt(value));
        } else if (key.equalsIgnoreCase("cache_expire")) {
            configure.setCacheExpire(Integer.parseInt(value));
        } else {
            return false;
        }
        return true;
    }

    private void configLogLevel(String value) {
        Logger rootLogger = Logger.getRootLogger();
        if ("debug".equalsIgnoreCase(value)) {
            rootLogger.setLevel(Level.DEBUG);
        } else if ("info".equalsIgnoreCase(value)) {
            rootLogger.setLevel(Level.INFO);
        } else if ("warn".equalsIgnoreCase(value)) {
            rootLogger.setLevel(Level.WARN);
        } else if ("trace".equalsIgnoreCase(value)) {
            rootLogger.setLevel(Level.TRACE);
        } else {
            return;
        }
        configure.setLoggerLevel(value);

    }

    /*
     * (non-Javadoc)
     *
     * @see us.codecraft.wifesays.me.ReloadAble#reload()
     */
    @Override
    public void reload() {
        if (!reloadOff) {
            readConfig(Configure.getConfigFilename());
        }
        threadPools.resize();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        readConfig(Configure.getConfigFilename());
    }

}
