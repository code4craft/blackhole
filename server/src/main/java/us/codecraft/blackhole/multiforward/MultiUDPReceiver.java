package us.codecraft.blackhole.multiforward;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

import us.codecraft.blackhole.blacklist.BlackListService;
import us.codecraft.blackhole.config.Configure;
import us.codecraft.blackhole.forward.DNSHostsContainer;

/**
 * @author yihua.huang@dianping.com
 * @date Jan 16, 2013
 */
@Component
public class MultiUDPReceiver {

	private ExecutorService receiveExecutors = Executors.newFixedThreadPool(4);
	private ExecutorService registerExecutors = Executors.newFixedThreadPool(4);

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

	public void registerReceiver(final DatagramChannel datagramChannel,
			final ForwardAnswer forwardAnswer) throws IOException {
		registerExecutors.submit(new Runnable() {

			@Override
			public void run() {
				final FutureTask<Object> future = new FutureTask<Object>(
						new Receiver(datagramChannel, forwardAnswer));
				receiveExecutors.execute(future);
				try {
					future.get(configure.getDnsTimeOut(), TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					future.cancel(true);
				}
			}
		});

	}

	private final class Receiver implements Callable<Object> {

		private DatagramChannel datagramChannel;

		private ForwardAnswer forwardAnswer;

		/**
		 * @param datagramChannel
		 * @param forwardAnswer
		 */
		public Receiver(DatagramChannel datagramChannel,
				ForwardAnswer forwardAnswer) {
			super();
			this.datagramChannel = datagramChannel;
			this.forwardAnswer = forwardAnswer;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Object call() throws Exception {
			long timeStart = System.currentTimeMillis();
			ByteBuffer byteBuffer = ByteBuffer.allocate(512);
			try {
				while (true) {
					byteBuffer.clear();
					SocketAddress remoteAddress = datagramChannel
							.receive(byteBuffer);
					long timeCost = System.currentTimeMillis() - timeStart;
					dnsHostsContainer.registerTimeCost(remoteAddress, timeCost);
					byte[] answer = Arrays.copyOfRange(byteBuffer.array(), 0,
							byteBuffer.remaining());
					Message message = new Message(answer);
					if (logger.isDebugEnabled()) {
						logger.debug("get message from " + remoteAddress + "\n"
								+ message);
					}
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
					checkReable(forwardAnswer.getQuery(), message);
				}
			} catch (ClosedByInterruptException e) {
			} catch (IOException e) {
				logger.warn("forward exception ", e);
			}
			return null;
		}
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
		boolean changed = false;
		for (Record answer : answers) {
			if ((answer.getType() == Type.A || answer.getType() == Type.AAAA)
					&& blackListService.inBlacklist(answer.rdataToString()
							.toString())) {
				if (!changed) {
					// copy on write
					message = (Message) message.clone();
				}
				message.removeRecord(answer, Section.ANSWER);
				changed = true;
			}
		}
		if (changed) {
			return message.toWire();
		}
		return bytes;
	}

}
