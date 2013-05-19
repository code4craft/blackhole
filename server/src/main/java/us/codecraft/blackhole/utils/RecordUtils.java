package us.codecraft.blackhole.utils;

import org.apache.commons.lang3.StringUtils;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
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

    public static boolean hasAnswer(Message message) {
        Record[] sectionArray = message.getSectionArray(Section.ANSWER);
        if (sectionArray == null || sectionArray.length == 0) {
            return false;
        } else {
            return true;
        }

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

    public static boolean isValidIpv4Address(String address) {
        if (StringUtils.isBlank(address)) {
            return false;
        }
        String[] items = address.split("\\.");
        if (items.length != 4) {
            return false;
        }
        for (String item : items) {
            try {
                int parseInt = Integer.parseInt(item);
                if (parseInt < 0 || parseInt > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    public static boolean areValidIpv4Addresses(String addresses) {
        if (StringUtils.isBlank(addresses)) {
            return false;
        }
        String[] items = addresses.split("\\,");
        for (String item : items) {
            if (!isValidIpv4Address(item)) {
                return false;
            }
        }
        return true;
    }
}
