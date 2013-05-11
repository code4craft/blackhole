package us.codecraft.blackhole.answer;

import org.springframework.stereotype.Component;
import us.codecraft.blackhole.context.RequestContext;
import us.codecraft.blackhole.utils.DoubleKeyMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class CustomTempAnswerProvider implements AnswerProvider {

	private Map<String,DoubleKeyMap<String, Integer, String>> container;

	public CustomTempAnswerProvider() {
		container = new ConcurrentHashMap<String, DoubleKeyMap<String, Integer, String>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.blackhole.answer.AnswerProvider#getAnswer(java.lang.String,
	 * int)
	 */
	@Override
	public String getAnswer(String query, int type) {
        String ip = RequestContext.getClientIp();
        DoubleKeyMap<String, Integer, String> stringIntegerStringDoubleKeyMap = container.get(ip);
        if (stringIntegerStringDoubleKeyMap==null){
            return null;
        }
        return stringIntegerStringDoubleKeyMap.get(query, type);
    }

	public void add(String clientIp,String query, int type, String answer) {
        DoubleKeyMap<String, Integer, String> stringIntegerStringDoubleKeyMap = container.get(clientIp);
        if (stringIntegerStringDoubleKeyMap==null){
            stringIntegerStringDoubleKeyMap = new DoubleKeyMap<String, Integer, String>(ConcurrentHashMap.class);
            container.put(clientIp,stringIntegerStringDoubleKeyMap);
        }
        stringIntegerStringDoubleKeyMap.put(query, type, answer);
	}

}
