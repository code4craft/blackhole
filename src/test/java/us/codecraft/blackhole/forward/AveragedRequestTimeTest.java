package us.codecraft.blackhole.forward;

import java.util.Random;
import java.util.TreeMap;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 22, 2012
 */
public class AveragedRequestTimeTest {

	private AveragedRequestTime averagedRequestTime;

	private static final double VERY_LITTLE_DOUBLE = 1e-10;

	@Before
	public void setUp() {
		averagedRequestTime = new AveragedRequestTime();
	}

	private double randomDouble() {
		Random random = new Random();
		return random.nextDouble();
	}

	private boolean approximatelyEquals(double a, double b) {
		return Math.abs(a - b) < VERY_LITTLE_DOUBLE;
	}

	@Test
	public void getAveragedTimeOfSameNumbersShouldBeSame() {
		double time1 = randomDouble();
		averagedRequestTime.add(time1);
		averagedRequestTime.add(time1);
		Assert.assertTrue(approximatelyEquals(averagedRequestTime.get(), time1));
	}

	@Test
	public void treeMapTest() {
		AveragedRequestTime t1 = new AveragedRequestTime();
		t1.add(1);
		AveragedRequestTime t2 = new AveragedRequestTime();
		t2.add(2);
		TreeMap<AveragedRequestTime, String> a = new TreeMap<AveragedRequestTime, String>();
		a.put(t1, "t1");
		a.put(t2, "t2");
		System.out.println(a.firstEntry().getValue());
		t2.clear();
		System.out.println(a.firstEntry().getValue());
		AveragedRequestTime t3 = new AveragedRequestTime();
		t3.add(3);
		a.put(t3, "t3");
		a.put(t2, "t2");
		System.out.println(a.firstEntry().getValue());
	}
}
