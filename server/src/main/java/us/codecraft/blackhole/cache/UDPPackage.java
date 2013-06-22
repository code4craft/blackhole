package us.codecraft.blackhole.cache;

import org.xbill.DNS.Message;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 19, 2012
 */
public class UDPPackage implements Serializable {

	private static final long serialVersionUID = 1L;
	private byte[] bytes;
	private static final int MASK = (1 << 8) - 1;

	public UDPPackage(byte[] bytes) {
		this.bytes = bytes;
	}

	/**
	 * copy on write
	 * 
	 * @param version
	 * @return
	 */
	public byte[] getBytes(int version) {
		byte[] bytes = Arrays.copyOf(this.bytes, this.bytes.length);
		bytes[1] = (byte) (version & MASK);
		bytes[0] = (byte) ((version >> 8) & MASK);
		return bytes;
	}

	/**
	 * block
	 * 
	 * @param version
	 * @return
	 */
	public synchronized byte[] getBytesSync(int version) {
		byte[] bytes = Arrays.copyOf(this.bytes, this.bytes.length);
		bytes[1] = (byte) (version & MASK);
		bytes[0] = (byte) ((version >> 8) & MASK);
		return bytes;
	}

	public static void main(String[] args) {
		byte[] b = new byte[] { 0, 1, 2 };
		UDPPackage package1 = new UDPPackage(b);
		package1.getBytes(128);
		System.out.println(package1);

		package1.getBytes(1);
		System.out.println(package1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
        try {
            final Message message = new Message(bytes);
            return message.toString();
        } catch (IOException e) {

            if (bytes == null || bytes.length == 0) {
                return "[]";
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            stringBuilder.append(bytes[0]);
            for (int i = 1; i < bytes.length; i++) {
                stringBuilder.append(",");
                stringBuilder.append(bytes[i]);
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }


	}
}
