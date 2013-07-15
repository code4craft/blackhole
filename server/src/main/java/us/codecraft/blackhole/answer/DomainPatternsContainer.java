package us.codecraft.blackhole.answer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yihua.huang@dianping.com <br>
 * @date: 13-7-15 <br>
 * Time: 上午8:18 <br>
 */
public class DomainPatternsContainer {

    private volatile Map<Pattern, String> domainPatterns = new HashMap<Pattern, String>();

    private volatile Map<String, String> domainTexts = new HashMap<String, String>();

    public Map<Pattern, String> getDomainPatterns() {
        return domainPatterns;
    }

    public void setDomainPatterns(Map<Pattern, String> domainPatterns) {
        this.domainPatterns = domainPatterns;
    }

    public Map<String, String> getDomainTexts() {
        return domainTexts;
    }

    public void setDomainTexts(Map<String, String> domainTexts) {
        this.domainTexts = domainTexts;
    }

    public String getIp(String domain) {
        String ip = domainTexts.get(domain);
        if (ip != null) {
            return ip;
        }
        for (Map.Entry<Pattern, String> entry : domainPatterns.entrySet()) {
            Matcher matcher = entry.getKey().matcher(domain);
            if (matcher.find()) {
                return entry.getValue();
            }
        }
        return null;
    }
}
