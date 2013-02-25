package us.codecraft.blackhole.multiforward;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

import us.codecraft.blackhole.cache.CacheManager;
import us.codecraft.blackhole.config.Configure;
import us.codecraft.blackhole.forward.DNSHostsContainer;
import us.codecraft.blackhole.safebox.BlackListService;
import us.codecraft.blackhole.safebox.SafeBoxService;

/**
 * @author yihua.huang@dianping.com
 * @date Jan 16, 2013
 */
@Component
public class MultiUDPReceiver implements InitializingBean {

	private Map<Integer, ForwardAnswer> answers = new ConcurrentHashMap<Integer, ForwardAnswer>();

	private DatagramChannel datagramChannel;

	private ExecutorService processExecutors = Executors.newFixedThreadPool(4);

	private final static int PORT_RECEIVE = 40311;

	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private BlackListService blackListService;

	@Autowired
	private DNSHostsContainer dnsHostsContainer;

	@Autowired
	private ConnectionTimer connectionTimer;

	@Autowired
	private SafeBoxService safeBoxService;

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
					&& blackListService.inBlacklist(address)) {
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

	public void registerReceiver(Integer id, ForwardAnswer forwardAnswer) {
		answers.put(id, forwardAnswer);
	}

	public ForwardAnswer getAnswer(Integer id) {
		return answers.get(id);
	}

	public void removeAnswer(Integer id) {
		answers.remove(id);
	}

	private void receive() {
		final ByteBuffer byteBuffer = ByteBuffer.allocate(512);
		while (true) {
			try {
				byteBuffer.clear();
				final SocketAddress remoteAddress = datagramChannel
						.receive(byteBuffer);
				processExecutors.submit(new Runnable() {

					@Override
					public void run() {
						try {
							handleAnswer(byteBuffer, remoteAddress);
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
		new Thread(new Runnable() {

			@Override
			public void run() {
				receive();

			}
		}).start();
	}

	private void addToBlacklist(Message message) {
		for (Record answer : message.getSectionArray(Section.ANSWER)) {
			String address = StringUtils.removeEnd(answer.rdataToString(), ".");
			if (!blackListService.inBlacklist(address)) {
				logger.info("detected dns poisoning, add address " + address
						+ " to blacklist");
				blackListService.addToBlacklist(address);
			}
		}
	}

	private void handleAnswer(ByteBuffer byteBuffer, SocketAddress remoteAddress)
			throws IOException {
		byte[] answer = Arrays.copyOfRange(byteBuffer.array(), 0,
				byteBuffer.remaining());
		final Message message = new Message(answer);
		// fake dns server return an answer, it must be dns poisoning
		if (remoteAddress.equals(configure.getFakeDnsServer())) {
			addToBlacklist(message);
			String domain = StringUtils.removeEnd(message.getQuestion()
					.getName().toString(), ".");
			safeBoxService.setPoisoned(domain);
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("get message from " + remoteAddress + "\n" + message);
		}
		final ForwardAnswer forwardAnswer = getAnswer(message.getHeader()
				.getID());
		if (forwardAnswer == null) {
			logger.warn("Oops!Received some unexpected messages! ");
			return;
		}
		dnsHostsContainer.registerTimeCost(remoteAddress,
				System.currentTimeMillis() - forwardAnswer.getStartTime());
		if (forwardAnswer.getAnswer() == null) {
			byte[] result = removeFakeAddress(message, answer);
			if (result != null) {
				forwardAnswer.setAnswer(result);
				try {
					forwardAnswer.getLock().lockInterruptibly();
					forwardAnswer.getCondition().signalAll();
				} catch (InterruptedException e) {
				} finally {
					forwardAnswer.getLock().unlock();
				}
			}
		}
		connectionTimer.checkConnectTimeForAnswer(forwardAnswer.getQuery(),
				message);

	}

	/**
	 * @return the datagramChannel
	 */
	public DatagramChannel getDatagramChannel() {
		return datagramChannel;
	}
}
