package us.codecraft.blackhole.utils;

import java.util.List;

/**
 * Like <a href="http://redis.io/topics/data-types">sortedset</a> in redis.<br>
 * 
 * 
 * @author yihua.huang@dianping.com
 * @date Dec 22, 2012
 * @param <V>
 *            type of value
 * @param <S>
 *            type of score
 */
public interface SortedSet<V, S extends Comparable<S>> {

	V add(V object, S score);

	S getScore(V object);

	V remove(V object);

	V last();

	V first();

	List<V> toList();

	V first(Selector<V, S> selector);

	V last(Selector<V, S> selector);

	List<V> toList(Selector<V, S> selector);

	void select(Selector<V, S> selector);

	List<V> remove(Selector<V, S> selector);

}
