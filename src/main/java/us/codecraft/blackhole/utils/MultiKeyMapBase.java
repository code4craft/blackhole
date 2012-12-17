package us.codecraft.blackhole.utils;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 14, 2012
 */
import java.util.HashMap;
import java.util.Map;

/**
 * 多个key的map，一些基础对象
 * 
 * @author yihua.huang
 * 
 */
public abstract class MultiKeyMapBase {
	@SuppressWarnings("rawtypes")
	private Class<? extends Map> protoMapClass = HashMap.class;

	public MultiKeyMapBase() {
	}

	@SuppressWarnings("rawtypes")
	public MultiKeyMapBase(Class<? extends Map> protoMapClass) {
		this.protoMapClass = protoMapClass;
	}

	@SuppressWarnings("unchecked")
	protected <K, V2> Map<K, V2> newMap() {
		try {
			return (Map<K, V2>) protoMapClass.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("wrong proto type map "
					+ protoMapClass);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("wrong proto type map "
					+ protoMapClass);
		}
	}
}