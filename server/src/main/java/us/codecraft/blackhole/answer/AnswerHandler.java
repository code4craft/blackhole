package us.codecraft.blackhole.answer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;
import us.codecraft.blackhole.container.Handler;
import us.codecraft.blackhole.container.MessageWrapper;
import us.codecraft.blackhole.utils.RecordBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component
public class AnswerHandler implements Handler, InitializingBean {

	private List<AnswerProvider> answerProviders;

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private CustomTempAnswerProvider customTempAnswerProvider;
	@Autowired
	private TempAnswerProvider tempAnswerContainer;

    @Autowired
    private CustomAnswerPatternProvider customAnswerPatternProvider;

	@Autowired
	private AnswerPatternProvider answerPatternContainer;

	@Autowired
	private SafeHostAnswerProvider safeHostAnswerProvider;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		regitestProviders();
	}

	public void regitestProviders() {
		answerProviders = new LinkedList<AnswerProvider>();
        answerProviders.add(customTempAnswerProvider);
        answerProviders.add(customAnswerPatternProvider);
        answerProviders.add(tempAnswerContainer);
        answerProviders.add(answerPatternContainer);
        answerProviders.add(safeHostAnswerProvider);
	}

	// b._dns-sd._udp.0.129.37.10.in-addr.arpa.
	private final Pattern filterPTRPattern = Pattern
			.compile(".*\\.(\\d+\\.\\d+\\.\\d+\\.\\d+\\.in-addr\\.arpa\\.)");

	private String filterPTRQuery(String query) {
		Matcher matcher = filterPTRPattern.matcher(query);
		if (matcher.matches()) {
			return matcher.group(1);
		} else {
			return query;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.blackhole.server.Handler#handle(org.xbill.DNS.Message,
	 * org.xbill.DNS.Message)
	 */
	@Override
	public boolean handle(MessageWrapper request, MessageWrapper response) {
		Record question = request.getMessage().getQuestion();
		String query = question.getName().toString();
		int type = question.getType();
		if (type == Type.PTR) {
			query = filterPTRQuery(query);
		}
		// some client will query with any
		if (type == Type.ANY) {
			type = Type.A;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("query \t" + Type.string(type) + "\t"
					+ DClass.string(question.getDClass()) + "\t" + query);
		}
		for (AnswerProvider answerProvider : answerProviders) {
			String answer = answerProvider.getAnswer(query, type);
			if (answer != null) {
				try {
					Record record = new RecordBuilder()
							.dclass(question.getDClass())
							.name(question.getName()).answer(answer).type(type)
							.toRecord();
					response.getMessage().addRecord(record, Section.ANSWER);
					if (logger.isDebugEnabled()) {
						logger.debug("answer\t" + Type.string(type) + "\t"
								+ DClass.string(question.getDClass()) + "\t"
								+ answer);
					}
					response.setHasRecord(true);
					return false;
				} catch (Throwable e) {
					logger.warn("handling exception " + e);
				}
			}
		}
		return true;
	}

}
