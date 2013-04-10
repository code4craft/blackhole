package us.codecraft.blackhole.utils;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 
 * @author yihua.huang@dianping.com
 * @date Apr 10, 2013
 */
public class QueueMap<K, V> {

	private Map<K, BlockingQueue<V>> map = new ConcurrentHashMap<K, BlockingQueue<V>>();

	public V poll(K key) {
		BlockingQueue<V> blockingQueue = map.get(key);
		if (blockingQueue == null) {
			return null;
		}
		return blockingQueue.poll();
	}

	public void add(K key, V value) {
		BlockingQueue<V> blockingQueue = map.get(key);
		if (blockingQueue == null) {
			synchronized (key.toString().intern()) {
				blockingQueue = map.get(key);
				if (blockingQueue == null) {
					blockingQueue = new LinkedBlockingDeque<V>();
					map.put(key, blockingQueue);
				}
			}
		}
		blockingQueue.add(value);
	}

	public void remove(K key) {
		BlockingQueue<V> blockingQueue = map.get(key);
		if (blockingQueue != null && blockingQueue.size() == 0) {
			synchronized (key.toString().intern()) {
				blockingQueue = map.get(key);
				if (blockingQueue != null && blockingQueue.size() == 0) {
					map.remove(key);
				}
			}
		}
	}
}
