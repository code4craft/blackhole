package us.codecraft.blackhole;

import java.util.Map;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
public class DoubleKeyMap<K1, K2, V> extends MultiKeyMapBase {
	private Map<K1, Map<K2, V>> map;

	public DoubleKeyMap() {
		init();
	}

	private void init() {
		map = this.<K1, Map<K2, V>> newMap();
	}

	/**
	 * 初始化map，使用protoMapClass作为构建内部map的原型
	 * 
	 * @param protoMapClass
	 *            构建内部map的类型
	 */
	@SuppressWarnings("rawtypes")
	public DoubleKeyMap(Class<? extends Map> protoMapClass) {
		super(protoMapClass);
		init();
	}

	/**
	 * 获取第一个key对应的map
	 * 
	 * @param key
	 *            key
	 * @return 单个key的map
	 */
	public Map<K2, V> get(K1 key) {
		return map.get(key);
	}

	/**
	 * 获取最终value值
	 * 
	 * @param key1
	 *            key1
	 * @param key2
	 *            key2
	 * @return 最终value值
	 */
	public V get(K1 key1, K2 key2) {
		if (get(key1) == null) {
			return null;
		}
		return get(key1).get(key2);
	}

	/**
	 * 添加一个元素
	 * 
	 * @param key1
	 *            key1
	 * @param key2
	 *            key2
	 * @param value
	 *            value
	 * @return 添加的value(根据内部map原型决定，可能永远为null)
	 */
	public V put(K1 key1, K2 key2, V value) {
		if (map.get(key1) == null) {
			map.put(key1, this.<K2, V> newMap());
		}
		return get(key1).put(key2, value);
	}

	/**
	 * 删除某一value的值
	 * 
	 * @param key1
	 *            key1
	 * @param key2
	 *            key2
	 * @return 删除的结果(根据内部map原型决定，可能永远为null)
	 */
	public V remove(K1 key1, K2 key2) {
		if (get(key1) == null) {
			return null;
		}
		V remove = get(key1).remove(key2);
		// 如果上一级map为空，把它也回收掉
		if (get(key1).size() == 0) {
			remove(key1);
		}
		return remove;
	}

	/**
	 * 删除第一个key对应的所有值
	 * 
	 * @param key1
	 *            key1
	 * @return 删除的map(根据内部map原型决定，可能永远为null)
	 */
	public Map<K2, V> remove(K1 key1) {
		Map<K2, V> remove = map.remove(key1);
		return remove;
	}
}
