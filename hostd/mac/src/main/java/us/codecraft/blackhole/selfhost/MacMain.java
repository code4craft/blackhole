package us.codecraft.blackhole.selfhost;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import us.codecraft.blackhole.config.Configure;
import us.codecraft.blackhole.utils.SpringLocator;
import us.codecraft.dnstools.InetConnectinoProperties;
import us.codecraft.dnstools.MacInetInetManager;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 27, 2012
 */
public class MacMain {

	private static int wifesaysPort = 40310;

	private static void startCheck() {
		Map<String, String> map = System.getenv();
		String username = map.get("USERNAME");
		if (!"root".equalsIgnoreCase(username)) {
			System.err.println("Sorry, blackhole must be started as root.");
			System.exit(-1);
		}
		try {
			new ServerSocket(wifesaysPort);
		} catch (IOException e) {
			System.err
					.println("Sorry, the "
							+ wifesaysPort
							+ " port is taken, check whether another instance is running.");
			System.exit(-1);
		}
	}

	private static void parseArgs(String[] args) {
		try {
			Options options = new Options();
			options.addOption(new Option("d", true, "home path"));
			CommandLineParser commandLineParser = new PosixParser();
			CommandLine commandLine = commandLineParser.parse(options, args);
			readOptions(commandLine);
		} catch (ParseException e) {
			System.err.println("Parse args error: " + e);
			System.exit(-1);
		}
	}

	private static void readOptions(CommandLine commandLine) {
		if (commandLine.hasOption("d")) {
			String filename = commandLine.getOptionValue("d");
			Configure.FILE_PATH = filename;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		parseArgs(args);
		startCheck();
		Configure.setZonesFilename("/etc/hostd");
		Configure.setConfigFilename("/etc/blackhole.conf");
		SpringLocator.applicationContext = new ClassPathXmlApplicationContext(
				"classpath*:/spring/applicationContext*.xml");
		DNSMonitor dnsMonitor = SpringLocator.getBean(DNSMonitor.class);
		MacInetInetManager macInetInetManager = new MacInetInetManager();
		InetConnectinoProperties defaultConnectionProperties = macInetInetManager
				.getDefaultConnectionProperties();
		dnsMonitor.setInetConnectinoProperties(defaultConnectionProperties);
		dnsMonitor.start();
	}

}
