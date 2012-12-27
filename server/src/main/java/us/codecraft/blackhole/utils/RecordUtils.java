package us.codecraft.blackhole.utils;

import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import us.codecraft.blackhole.config.Configure;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 19, 2012
 */
public class RecordUtils {

	public static long maxTTL(Record[] records) {
		if (records == null || records.length == 0) {
			return Configure.DEFAULT_TTL;
		}
		long max = 0;
		for (Record record : records) {
			if (record.getTTL() > max) {
				max = record.getTTL();
			}
		}
		return max;
	}

	public static long minTTL(Record[] records) {
		if (records == null || records.length == 0) {
			return Configure.DEFAULT_TTL;
		}
		long min = Integer.MAX_VALUE;
		for (Record record : records) {
			if (record.getTTL() < min) {
				min = record.getTTL();
			}
		}
		return min;
	}

	public static String recordKey(Record record) {
		return record.getName().toString() + " "
				+ Type.string(record.getType());
	}
}
