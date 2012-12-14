package us.codecraft.blackhole.server;

import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
public class HeaderHandler implements Handler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.blackhole.server.Handler#handle(org.xbill.DNS.Message,
	 * org.xbill.DNS.Message)
	 */
	@Override
	public boolean handle(Message request, Message response) {
		response.getHeader().setFlag(Flags.QR);
		if (request.getHeader().getFlag(Flags.RD)) {
			response.getHeader().setFlag(Flags.RD);
		}
		Record queryRecord = request.getQuestion();
		response.addRecord(queryRecord, Section.QUESTION);
		return true;
	}

}
