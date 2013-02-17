package us.codecraft.blackhole.forward.concurrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import org.springframework.stereotype.Component;

/**
 * @author yihua.huang@dianping.com
 * @date Jan 16, 2013
 */
@Component
public class MultiSender {

	public void send() {
		try {
			DatagramChannel channel = DatagramChannel.open();
			channel.bind(new InetSocketAddress(10001));
			ByteBuffer byteBuffer = ByteBuffer.allocate(500);
			for (int j = 0; j < 10; j++) {
				byteBuffer.put("asdasdasd".getBytes());
			}
			for (int i = 0; i < 5000; i++) {
				channel.send(byteBuffer, new InetSocketAddress("127.0.0.1",
						10000));
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
