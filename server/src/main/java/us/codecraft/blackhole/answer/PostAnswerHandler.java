package us.codecraft.blackhole.answer;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
@Component("postAnswerHandler")
public class PostAnswerHandler extends AbstractAnswerHandler implements InitializingBean {

    private List<AnswerProvider> answerProviders;

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
    }

    @Override
    protected List<AnswerProvider> getaAnswerProviders() {
        return answerProviders;
    }
}
