package us.codecraft.blackhole.forward;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 22, 2012
 */
public class AveragedRequestTime implements Comparable<AveragedRequestTime> {

	private int count = 0;

	private double averageTime = 1e-12;

	private AtomicInteger failCount = new AtomicInteger();

	public synchronized void add(double time) {
		double totalTime = averageTime * count + time;
		count++;
		averageTime = totalTime / count;
	}

	public void incrFailCount() {
		failCount.incrementAndGet();
	}

	public synchronized void clear() {
		count = 0;
		averageTime = 1e-12;
		failCount.set(0);
	}

	public Double get() {
		return averageTime;
	}

	public double getFailRate() {
		if (count == failCount.get()) {
			return 0.5;
		} else {
			return failCount.get() / (count + failCount.get());
		}
	}

	public Double getAverageWithTimeout(long timeout) {
		if (failCount.get() == 0) {
			return averageTime;
		}
		return (averageTime * count + failCount.get() * timeout)
				/ (count + failCount.get());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AveragedRequestTime o) {
		return this.get().compareTo(o.get());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AveragedRequestTime [count=" + count + ", averageTime="
				+ averageTime + ", failCount=" + failCount + "]";
	}

}
