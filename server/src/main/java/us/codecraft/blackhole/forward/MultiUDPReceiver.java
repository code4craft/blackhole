package us.codecraft.blackhole.forward;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Message;
import us.codecraft.blackhole.concurrent.ThreadPools;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;

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
         * @param initDelay in ms
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
    private ThreadPools threadPools;

    @Autowired
    private ForwardAnswerProcessor forwardAnswerProcessor;

    private Logger logger = Logger.getLogger(getClass());

    /**
     *
     */
    public MultiUDPReceiver() {
        super();
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

    /**
     * Add answer to remove queue and remove when timeout.
     * @param message
     * @param timeOut
     */
    public void delayRemoveAnswer(Message message, long timeOut) {
        delayRemoveQueue.add(new DelayStringKey(getKey(message), timeOut));
    }

    private void receive() {
        ExecutorService processExecutors = threadPools.getUdpReceiverExecutor();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(dnsPackageLength);
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
                            final Message message = new Message(answer);
                            forwardAnswerProcessor.handleAnswer(answer, message, remoteAddress, getAnswer(message));
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
        try {
            datagramChannel.socket().bind(new InetSocketAddress(PORT_RECEIVE));
        } catch (IOException e) {
            System.err.println("Startup fail, maybe another process is running.");
            logger.error("Startup fail, maybe another process is running.", e);
            System.exit(-1);
        }
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
                ForwardAnswer forwardAnswer = answers.get(delayRemoveKey.getKey());
                if (forwardAnswer != null && forwardAnswer.getTempAnswer() != null) {
                    forwardAnswer.getResponser().response(forwardAnswer.getTempAnswer().toWire());
                }
                answers.remove(delayRemoveKey.getKey());
                if (logger.isDebugEnabled()) {
                    logger.debug("remove key " + delayRemoveKey.getKey());
                }
            } catch (Exception e) {
                logger.warn("remove answer error", e);
            }
        }
    }

    /**
     * @return the datagramChannel
     */
    public DatagramChannel getDatagramChannel() {
        return datagramChannel;
    }
}
