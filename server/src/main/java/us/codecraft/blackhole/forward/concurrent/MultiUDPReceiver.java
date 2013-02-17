package us.codecraft.blackhole.forward.concurrent;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;

import us.codecraft.blackhole.config.Configure;

/**
 * @author yihua.huang@dianping.com
 * @date Jan 16, 2013
 */
@Component
public class MultiUDPReceiver {

	private ExecutorService executorService = Executors.newFixedThreadPool(4);

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
		new Thread(new Runnable() {

			@Override
			public void run() {
				final FutureTask<Object> future = new FutureTask<Object>(
						new Receiver(datagramChannel, forwardAnswer));
				executorService.execute(future);
				try {
					future.get(configure.getDnsTimeOut(), TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					future.cancel(true);
				}
			}
		}).start();

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
			ByteBuffer byteBuffer = ByteBuffer.allocate(512);
			try {
				while (true) {
					byteBuffer.clear();
					SocketAddress receive = datagramChannel.receive(byteBuffer);
					byte[] answer = Arrays.copyOfRange(byteBuffer.array(), 0,
							byteBuffer.remaining());
					if (forwardAnswer.getAnswer() == null) {
						forwardAnswer.setAnswer(answer);
						try {
							forwardAnswer.getLock().lockInterruptibly();
							forwardAnswer.getCondition().signal();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							forwardAnswer.getLock().unlock();
						}
					}
					Message message = new Message(answer);
					logger.info("get message from " + receive + "\n" + message);
				}
			} catch (ClosedByInterruptException e) {
			} catch (IOException e) {
				logger.warn("forward exception ", e);
			}
			return null;
		}
	}

}
