package us.codecraft.blackhole.multiforward;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
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

import us.codecraft.blackhole.blacklist.BlackListService;
import us.codecraft.blackhole.cache.CacheManager;
import us.codecraft.blackhole.config.Configure;
import us.codecraft.blackhole.forward.DNSHostsContainer;

/**
 * @author yihua.huang@dianping.com
 * @date Jan 16, 2013
 */
@Component
public class MultiUDPReceiver implements InitializingBean {

	private Map<Integer, ForwardAnswer> answers = new ConcurrentHashMap<Integer, ForwardAnswer>();

	private DatagramChannel datagramChannel;

	private ExecutorService processExecutors = Executors.newFixedThreadPool(4);

	private ExecutorService checkExecutors = Executors.newFixedThreadPool(4);

	private final static int PORT_RECEIVE = 40311;

	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private BlackListService blackListService;

	@Autowired
	private DNSHostsContainer dnsHostsContainer;

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private Configure configure;

	/**
	 * 
	 */
	public MultiUDPReceiver() {
		super();
	}

	private void checkReable(Message query, Message message) {
		Record[] answers = message.getSectionArray(Section.ANSWER);
		for (Record answer : answers) {
			if (answer.getType() == Type.A || answer.getType() == Type.AAAA) {
				String address = StringUtils.removeEnd(answer.rdataToString(),
						".");
				try {
					if (logger.isDebugEnabled()) {
						logger.debug("try to reach host " + address);
					}
					if (!InetAddress.getByName(address).isReachable(1000)) {
						blackListService.registerInvalidAddress(query, address);
					} else {
						cacheManager.setToCache(query, message.toWire());
					}
				} catch (UnknownHostException e) {
					logger.warn("unkown host " + address + " " + e);
				} catch (IOException e) {
					logger.warn("ping " + address + " error " + e);
				}
			}
		}
	}

	private byte[] removeFakeAddress(Message message, byte[] bytes) {
		Record[] answers = message.getSectionArray(Section.ANSWER);
		if (answers == null || answers.length == 0) {
			return null;
		}
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
		if (changed) {
			if (message.getSectionArray(Section.ANSWER) == null
					|| message.getSectionArray(Section.ANSWER).length == 0) {
				return null;
			}
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
							logger.warn("forward exception ", e);
						}
					}
				});

			} catch (Throwable e) {
				logger.warn("receive exception", e);
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
		datagramChannel.bind(new InetSocketAddress(PORT_RECEIVE));
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
			logger.info("detected dns poisoning, add address " + address
					+ " to blacklist");
			blackListService.addToBlacklist(address);
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
					forwardAnswer.getCondition().signal();
				} catch (InterruptedException e) {
				} finally {
					forwardAnswer.getLock().unlock();
				}
			}
		}
		checkExecutors.submit(new Runnable() {

			@Override
			public void run() {
				try {
					checkReable(forwardAnswer.getQuery(), message);
				} catch (Throwable e) {
					logger.warn("check error ", e);
				}

			}
		});

	}

	/**
	 * @return the datagramChannel
	 */
	public DatagramChannel getDatagramChannel() {
		return datagramChannel;
	}
}
