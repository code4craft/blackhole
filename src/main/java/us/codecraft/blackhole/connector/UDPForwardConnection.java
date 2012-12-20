package us.codecraft.blackhole.connector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Record;

import us.codecraft.blackhole.config.Configure;
import us.codecraft.blackhole.utils.StatefulLock;

/**
 * WTF ugly concurrent coding style! Fortunately it works!<br/>
 * 
 * 
 * @author yihua.huang@dianping.com
 * @date 2012-12-15
 */
@Component
public class UDPForwardConnection {

	public static UDPForwardConnection INSTANCE = new UDPForwardConnection();

	private Logger logger = Logger.getLogger(getClass());

	private volatile boolean inited = false;
	private static final int IS_NOT_READABLE = 0;
	private static final int IS_READABLE = 1;

	private volatile Selector selector;

	@Autowired
	private Configure configure;

	public UDPForwardConnection() {
		getSelector();
	}

	private Selector getSelector() {
		if (selector == null) {
			synchronized (this) {
				try {
					selector = Selector.open();
				} catch (IOException e) {
					logger.warn("init selector failed ", e);
				}
			}
		}
		return selector;
	}

	public byte[] forward(Record question, byte[] query) {
		if (configure.getDnsHost() == null) {
			logger.warn("The forward DNS server is not configured!");
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("No record, forwarding to " + configure.getDnsHost()
					+ ":" + Configure.DNS_PORT);
		}
		DatagramChannel datagramChannel = null;
		ByteBuffer byteBuffer = ByteBuffer.allocate(512);
		try {
			datagramChannel = DatagramChannel.open();

			SocketAddress address = new InetSocketAddress(
					configure.getDnsHost(), Configure.DNS_PORT);
			datagramChannel.connect(address);
			byteBuffer.clear();
			byteBuffer.put(query);
			byteBuffer.flip();
			datagramChannel.write(byteBuffer);
			datagramChannel.configureBlocking(false);
			StatefulLock lock = new StatefulLock(IS_NOT_READABLE);
			Selector selector = getSelector();
			if (selector == null) {
				logger.warn("Selector has not initted.");
				return null;
			}
			datagramChannel.register(selector, SelectionKey.OP_READ, lock);
			init();
			// wait until select thread ready
			synchronized (lock) {
				try {
					lock.wait(configure.getDnsTimeOut());
				} catch (InterruptedException e) {
				}
			}
			if (lock.getState() == IS_NOT_READABLE) {
				if (logger.isDebugEnabled()) {
					logger.debug("Read time out.");
				}
				return null;
			}
			byteBuffer.clear();
			datagramChannel.read(byteBuffer);
			byteBuffer.flip();
		} catch (IOException e) {
			logger.warn("io exception! ", e);
		} finally {
			try {
				datagramChannel.close();
			} catch (IOException e1) {
			}
		}
		byte[] copyOfRange = Arrays.copyOfRange(byteBuffer.array(), 0,
				byteBuffer.remaining());
		return copyOfRange;
	}

	private void init() {
		if (getSelector() == null) {
			logger.warn("Selector has not initted.");
			return;
		}
		synchronized (this) {
			if (!inited) {
				inited = true;
			} else {
				return;
			}
		}
		new Thread() {
			public void run() {
				selectLoop();
			};
		}.start();
	}

	private void selectLoop() {
		logger.info("init loop!");
		while (true) {
			try {
				selector.select();
				Iterator<SelectionKey> iterator = selector.selectedKeys()
						.iterator();
				while (iterator.hasNext()) {
					SelectionKey selectionKey = iterator.next();
					iterator.remove();
					if (selectionKey.isValid() && selectionKey.isReadable()) {
						StatefulLock lock = (StatefulLock) selectionKey
								.attachment();
						synchronized (lock) {
							lock.setState(IS_READABLE);
							lock.notifyAll();
						}

					}
				}
			} catch (IOException e) {
				logger.warn("failed ");
			}
		}
	}
}
