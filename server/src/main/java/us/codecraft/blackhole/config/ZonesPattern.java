package us.codecraft.blackhole.config;


import org.apache.commons.lang3.StringUtils;
import org.xbill.DNS.Address;
import us.codecraft.blackhole.answer.AnswerPatternProvider;
import us.codecraft.blackhole.utils.RecordUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * User: cairne
 * Date: 13-5-11
 * Time: 下午9:02
 */
public class ZonesPattern {

    private String userIp;

    private String targetIp;

    private List<Pattern> patterns = new ArrayList<Pattern>();

    private List<String> texts = new ArrayList<String>();

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }

    public static ZonesPattern parse(String line) throws UnknownHostException {
        ZonesPattern zonesPattern = new ZonesPattern();
        line = line.trim();
        if (line.startsWith("#")) {
            return null;
        }
        if (line.contains(":")) {
            String userIp = StringUtils.trim(StringUtils.substringBefore(line, ":"));
            zonesPattern.setUserIp(userIp);
            line = StringUtils.trim(StringUtils.substringAfter(line, ":"));
            Address.getByAddress(userIp);
        }
        String[] items = line.split("[\\s_]+");
        if (items.length < 2) {
            return null;
        }
        if (items[0].equalsIgnoreCase("NS")) {
            boolean configIp = RecordUtils
                    .areValidIpv4Addresses(items[1]);
            zonesPattern.setTargetIp(AnswerPatternProvider.DO_NOTHING);
            for (int i = configIp ? 2 : 1; i < items.length; i++) {
                String pattern = items[i];
                parseDomainPattern(zonesPattern, pattern);
            }

        } else {
            String ip = items[0];
            Address.getByAddress(ip);
            zonesPattern.setTargetIp(ip);
            for (int i = 1; i < items.length; i++) {
                String pattern = items[i];
                parseDomainPattern(zonesPattern, pattern);
            }
        }

        return zonesPattern;
    }

    private static void parseDomainPattern(ZonesPattern zonesPattern, String pattern) {
        DomainPattern domainPattern = DomainPattern.parse(pattern);
        if (domainPattern.isUseRegex()) {
            zonesPattern.getPatterns().add(domainPattern.getRegexPattern());
        } else {
            zonesPattern.getTexts().add(domainPattern.getFullTextMatch());
        }
    }


}
