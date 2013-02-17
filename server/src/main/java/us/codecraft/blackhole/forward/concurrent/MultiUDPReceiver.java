package us.codecraft.blackhole.forward.concurrent;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;

/**
 * @author yihua.huang@dianping.com
 * @date Jan 16, 2013
 */
@Component
public class MultiUDPReceiver implements InitializingBean {

	private Selector selector;

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * 
	 */
	public MultiUDPReceiver() {
		super();
	}

	public synchronized void registerReceiver(DatagramChannel datagramChannel,
			ForwardAnswer forwardAnswer) throws IOException {
		datagramChannel.configureBlocking(false);
		datagramChannel.register(selector, SelectionKey.OP_READ, forwardAnswer);
	}

	public void receive() {
		try {
			while (true) // 不断的轮询
			{
				selector.select();
				Iterator<SelectionKey> selectionKeyIterator = selector
						.selectedKeys().iterator();
				while (selectionKeyIterator.hasNext()) {
					SelectionKey selectionKey = (SelectionKey) selectionKeyIterator
							.next();
					selectionKeyIterator.remove();
					processSelectionKey(selector, selectionKey);
				}
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processSelectionKey(Selector selector,
			SelectionKey selectionKey) throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.allocate(512);
		if (selectionKey.isReadable()) {
			DatagramChannel datagramChannel = (DatagramChannel) selectionKey
					.channel();
			ForwardAnswer forwardAnswer = (ForwardAnswer) selectionKey
					.attachment();
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		selector = Selector.open();
		new Thread(new Runnable() {

			@Override
			public void run() {
				receive();

			}
		}).start();
	}

}
