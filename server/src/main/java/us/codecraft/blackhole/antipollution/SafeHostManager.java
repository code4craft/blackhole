package us.codecraft.blackhole.antipollution;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import us.codecraft.blackhole.config.Configure;
import us.codecraft.wifesays.me.ShutDownAble;
import us.codecraft.wifesays.me.StandReadyWorker;

import java.io.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yihua.huang@dianping.com
 * @date Feb 20, 2013
 */
@Component
public class SafeHostManager extends StandReadyWorker implements
		InitializingBean, ShutDownAble {

	private Logger logger = Logger.getLogger(getClass());

	private Map<String, Boolean> poisons = new ConcurrentHashMap<String, Boolean>();

	private Map<String, String> answers = new ConcurrentHashMap<String, String>();

	private static final String FLUSH_CMD = "flush";

	public void flushToFile(String filename) throws IOException {
		PrintWriter writer = new PrintWriter(new File(filename));
		for (Entry<String, String> address : answers.entrySet()) {
			writer.println(address.getValue() + "\t" + address.getKey());
		}
        filename.charAt(1);
		writer.close();
	}

	public void loadFromFile(String filename) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				new File(filename)));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("#")) {
				break;
			}
			String[] split = line.split("\\s");
			if (split.length <= 1) {
				logger.info("error record \"" + line + "\", ignored.");
			}
			answers.put(split[1], split[0]);
			poisons.put(split[1], Boolean.TRUE);
			if (logger.isDebugEnabled()) {
				logger.debug("load blacklist address " + line);
			}
		}
		bufferedReader.close();
	}

	public void add(String domain, String address) {
		answers.put(domain, address);
	}

	public boolean isPoisoned(String domain) {
		Boolean poisoned = poisons.get(domain);
		if (poisoned == null) {
			return false;
		}
		return poisoned;
	}

	public void setPoisoned(String domain) {
		poisons.put(domain, Boolean.TRUE);
	}

	public String get(String domain) {
		return answers.get(domain);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.wifesays.me.ShutDownAble#shutDown()
	 */
	@Override
	public void shutDown() {
		String filename = Configure.FILE_PATH + "/safehost";
		try {
			flushToFile(filename);
		} catch (IOException e) {
			logger.warn("write to file " + filename + " error! " + e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		String filename = Configure.FILE_PATH + "/safehost";
		try {
			loadFromFile(filename);
		} catch (IOException e) {
			logger.warn("load file " + filename + " error! " + e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.wifesays.me.StandReady#doWhatYouShouldDo(java.lang.String)
	 */
	@Override
	public String doWhatYouShouldDo(String whatWifeSays) {
		if (FLUSH_CMD.equalsIgnoreCase(whatWifeSays)) {
			String filename = Configure.FILE_PATH + "/safehost";
			try {
				flushToFile(filename);
			} catch (IOException e) {
				logger.warn("write to file " + filename + " error! " + e);
			}
			return "SUCCESS";
		}
		return null;
	}

}
