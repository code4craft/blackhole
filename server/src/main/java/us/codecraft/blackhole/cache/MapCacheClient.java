package us.codecraft.blackhole.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache in ConcurrentHashMap. Just for test.<br>
 * Not in use.<br>
 * @author code4crafer@gmail.com
 *         Date: 13-6-23
 *         Time: 下午12:08
 */
@Component("MapCacheClient")
public class MapCacheClient implements CacheClient{

    private Map<String,Object> map = new ConcurrentHashMap<String, Object>();

    @Override
    public <T> boolean set(String key, T value, int expireTime) {
        map.put(key,value);
        return true;
    }

    @Override
    public <T> T get(String key) {
        return (T)map.get(key);
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void clearCache() {
        map = new ConcurrentHashMap<String, Object>();
    }
}
