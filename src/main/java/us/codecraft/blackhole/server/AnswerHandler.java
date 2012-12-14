package us.codecraft.blackhole.server;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import us.codecraft.blackhole.RecordBuilder;
import us.codecraft.blackhole.zones.AnswerCacheContainer;
import us.codecraft.blackhole.zones.AnswerProvider;
import us.codecraft.blackhole.zones.PatternContainer;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
public class AnswerHandler implements Handler {

	private List<AnswerProvider> answerProviders;

	private Logger logger = Logger.getLogger(getClass());

	AnswerHandler() {
		init();
	}

	public void init() {
		answerProviders = new LinkedList<AnswerProvider>();

		answerProviders.add(AnswerCacheContainer.instance());
		answerProviders.add(PatternContainer.instance());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.blackhole.server.Handler#handle(org.xbill.DNS.Message,
	 * org.xbill.DNS.Message)
	 */
	@Override
	public boolean handle(Message request, Message response) {
		Record question = request.getQuestion();
		String string = question.getName().toString();
		logger.info("query \t" + Type.string(question.getType()) + "\t"
				+ DClass.string(question.getDClass()) + "\t" + string);
		for (AnswerProvider answerProvider : answerProviders) {
			String answer = answerProvider
					.getAnswer(string, question.getType());
			if (answer != null) {
				try {
					Record record = new RecordBuilder()
							.dclass(question.getDClass())
							.name(question.getName()).answer(answer)
							.type(question.getType()).toRecord();
					response.addRecord(record, Section.ANSWER);
					logger.info("answer\t" + Type.string(question.getType())
							+ "\t" + DClass.string(question.getDClass()) + "\t"
							+ answer);
					return true;
				} catch (UnknownHostException e) {
					logger.warn("handling exception ", e);
				} catch (TextParseException e) {
					logger.warn("handling exception ", e);
				}
			}
		}
		return true;
	}

}
