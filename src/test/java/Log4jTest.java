import org.apache.log4j.Logger;
import org.junit.Test;


public class Log4jTest {
	
	private Logger log = Logger.getLogger(getClass());
	
	@Test
	public void test(){
		log.info("yes");
	}

}
