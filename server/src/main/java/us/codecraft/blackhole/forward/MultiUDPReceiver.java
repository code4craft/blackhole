package us.codecraft.blackhole.forward;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

import us.codecraft.blackhole.antipollution.BlackListManager;
import us.codecraft.blackhole.antipollution.SafeHostManager;
import us.codecraft.blackhole.cache.CacheManager;
import us.codecraft.blackhole.config.Configure;
import us.codecraft.blackhole.concurrent.ThreadPools;

/**
 * Listen on port 40311 using reactor mode.
 * 
 * @author yihua.huang@dianping.com
 * @date Jan 16, 2013
 */
@Component
public class MultiUDPReceiver implements InitializingBean {

	/**
	 * 
	 */
	private static final int dnsPackageLength = 512;

	private Map<String, ForwardAnswer> answers = new ConcurrentHashMap<String, ForwardAnswer>();

	private DatagramChannel datagramChannel;

	private final static int PORT_RECEIVE = 40311;

	private DelayQueue<DelayStringKey> delayRemoveQueue = new DelayQueue<DelayStringKey>();

	private static class DelayStringKey implements Delayed {

		private final String key;

		private final long initDelay;

		private long startTime;

		/**
		 * @param key
		 * @param initDelay
		 *            in ms
		 */
		public DelayStringKey(String key, long initDelay) {
			this.startTime = System.currentTimeMillis();
			this.key = key;
			this.initDelay = initDelay;
		}

		public String getKey() {
			return key;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Delayed o) {
			long delayA = startTime + initDelay - System.currentTimeMillis();
			long delayB = o.getDelay(TimeUnit.MILLISECONDS);
			if (delayA > delayB) {
				return 1;
			} else if (delayA < delayB) {
				return -1;
			} else {
				return 0;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit)
		 */
		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(
					startTime + initDelay - System.currentTimeMillis(),
					TimeUnit.MILLISECONDS);
		}

	}

	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private BlackListManager blackListManager;
	@Autowired
	private DNSHostsContainer dnsHostsContainer;

	@Autowired
	private ConnectionTimer connectionTimer;

	@Autowired
	private SafeHostManager safeBoxService;
	@Autowired
	private ThreadPools threadPools;

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private Configure configure;

	/**
	 * 
	 */
	public MultiUDPReceiver() {
		super();
	}

	private byte[] removeFakeAddress(Message message, byte[] bytes) {
		Record[] answers = message.getSectionArray(Section.ANSWER);
		boolean changed = false;
		for (Record answer : answers) {
			String address = StringUtils.removeEnd(answer.rdataToString(), ".");
			if ((answer.getType() == Type.A || answer.getType() == Type.AAAA)
					&& blackListManager.inBlacklist(address)) {
				if (!changed) {
					// copy on write
					message = (Message) message.clone();
				}
				message.removeRecord(answer, Section.ANSWER);
				changed = true;
			}
		}
		if (message.getQuestion().getType() == Type.A
				&& (message.getSectionArray(Section.ANSWER) == null || message
						.getSectionArray(Section.ANSWER).length == 0)
				&& (message.getSectionArray(Section.ADDITIONAL) == null || message
						.getSectionArray(Section.ADDITIONAL).length == 0)
				&& (message.getSectionArray(Section.AUTHORITY) == null || message
						.getSectionArray(Section.AUTHORITY).length == 0)) {
			logger.info("remove message " + message.getQuestion());
			return null;
		}
		if (changed) {
			return message.toWire();
		}
		return bytes;
	}

	private String getKey(Message message) {
		return message.getHeader().getID() + "_"
				+ message.getQuestion().getName().toString() + "_"
				+ message.getQuestion().getType();
	}

	public void registerReceiver(Message message, ForwardAnswer forwardAnswer) {
		answers.put(getKey(message), forwardAnswer);
	}

	public ForwardAnswer getAnswer(Message message) {
		return answers.get(getKey(message));
	}

	public void removeAnswer(Message message, long timeOut) {
		delayRemoveQueue.add(new DelayStringKey(getKey(message), timeOut));
	}

	private void receive() {
		ExecutorService processExecutors = threadPools.getMainProcessExecutor();
		final ByteBuffer byteBuffer = ByteBuffer.allocate(512);
		while (true) {
			try {
				byteBuffer.clear();
				final SocketAddress remoteAddress = datagramChannel
						.receive(byteBuffer);
				final byte[] answer = Arrays.copyOfRange(byteBuffer.array(), 0,
						dnsPackageLength);
				processExecutors.submit(new Runnable() {

					@Override
					public void run() {
						try {
							handleAnswer(answer, remoteAddress);
						} catch (Throwable e) {
							logger.warn("forward exception " + e);
						}
					}
				});

			} catch (Throwable e) {
				logger.warn("receive exception" + e);
			}
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
		datagramChannel = DatagramChannel.open();
		datagramChannel.socket().bind(new InetSocketAddress(PORT_RECEIVE));
		datagramChannel.configureBlocking(true);
        Thread threadForReceive = new Thread(new Runnable() {

            @Override
            public void run() {
                receive();

            }
        });
        threadForReceive.setDaemon(true);
        threadForReceive.start();
        Thread threadForRemove = new Thread(new Runnable() {

            @Override
            public void run() {
                removeAnswerFromQueue();

            }
        });
        threadForRemove.setDaemon(true);
        threadForRemove.start();
	}

	private void removeAnswerFromQueue() {
		while (true) {
			try {
				DelayStringKey delayRemoveKey = delayRemoveQueue.take();
				answers.remove(delayRemoveKey.getKey());
				if (logger.isDebugEnabled()) {
					logger.debug("remove key " + delayRemoveKey.getKey());
				}
			} catch (Exception e) {
				logger.warn("remove answer error", e);
			}
		}
	}

	private void addToBlacklist(Message message) {
		for (Record answer : message.getSectionArray(Section.ANSWER)) {
			String address = StringUtils.removeEnd(answer.rdataToString(), ".");
			if (!blackListManager.inBlacklist(address)) {
				logger.info("detected dns poisoning, add address " + address
						+ " to blacklist");
				blackListManager.addToBlacklist(address);
			}
		}
	}

	private void handleAnswer(byte[] answer, SocketAddress remoteAddress)
			throws IOException {
		final Message message = new Message(answer);
		// fake dns server return an answer, it must be dns pollution
		if (configure.getFakeDnsServer() != null
				&& remoteAddress.equals(configure.getFakeDnsServer())) {
			addToBlacklist(message);
			String domain = StringUtils.removeEnd(message.getQuestion()
					.getName().toString(), ".");
			safeBoxService.setPoisoned(domain);
			return;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("get message from " + remoteAddress + "\n" + message);
		}
		final ForwardAnswer forwardAnswer = getAnswer(message);
		if (forwardAnswer == null) {
			logger.info("Received messages for "
					+ message.getQuestion().getName().toString()
					+ " after timeout!");
			return;
		}
		dnsHostsContainer.registerTimeCost(remoteAddress,
				System.currentTimeMillis() - forwardAnswer.getStartTime());
		answer = removeFakeAddress(message, answer);
		if (logger.isDebugEnabled()) {
			logger.debug("response message " + message.getHeader().getID()
					+ " to "
					+ forwardAnswer.getResponser().getInDataPacket().getPort());
		}
		if (answer != null) {
			forwardAnswer.getResponser().response(answer);
			cacheManager.setToCache(message, answer);
		}
		// connectionTimer.checkConnectTimeForAnswer(forwardAnswer.getQuery(),
		// message);

	}

	/**
	 * @return the datagramChannel
	 */
	public DatagramChannel getDatagramChannel() {
		return datagramChannel;
	}
}
