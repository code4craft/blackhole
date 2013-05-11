package us.codecraft.blackhole.context;

/**
 * User: cairne
 * Date: 13-5-11
 * Time: 下午7:56
 */
public class RequestContext {

    private static ThreadLocal<String> clientIps = new ThreadLocal<String>();

    public static String getClientIp() {
        return clientIps.get();
    }

    public static void setClientIps(String clientIp){
        clientIps.set(clientIp);
    }


}
