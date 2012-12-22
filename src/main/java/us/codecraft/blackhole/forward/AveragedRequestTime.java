package us.codecraft.blackhole.forward;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 22, 2012
 */
public class AveragedRequestTime implements Comparable<AveragedRequestTime> {

	private int count = 0;

	private double averageTime = 1e-12;

	public synchronized void add(double time) {
		double totalTime = averageTime * count + time;
		count++;
		averageTime = totalTime / count;
	}

	public synchronized void clear() {
		count = 0;
		averageTime = 1e-12;
	}

	public Double get() {
		return averageTime;
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
				+ averageTime + "]";
	}

}
