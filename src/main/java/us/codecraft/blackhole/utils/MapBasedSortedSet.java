package us.codecraft.blackhole.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * 
 * @author yihua.huang
 * 
 */
public class MapBasedSortedSet<V, S extends Comparable<S>> implements
		SortedSet<V, S> {

	private static class WeightedObject<V, S extends Comparable<S>> implements
			Comparable<WeightedObject<V, S>> {
		private final V value;
		private final S score;

		/**
		 * 
		 * @param value
		 * @param score
		 */
		public WeightedObject(V value, S score) {
			super();
			this.score = score;
			this.value = value;
		}

		/**
		 * (non-Jsdoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(WeightedObject<V, S> o) {
			if (this.score.compareTo(o.score) == 0) {
				if (this.value instanceof Comparable) {
					return ((Comparable<V>) this.value).compareTo(o.value);
				}
				return new Integer(this.hashCode()).compareTo(o.hashCode());
			}
			return this.score.compareTo(o.score);
		}
	}

	private NavigableMap<WeightedObject<V, S>, V> orderMap;

	private Map<V, WeightedObject<V, S>> reverseMap;

	public MapBasedSortedSet() {
		orderMap = new TreeMap<WeightedObject<V, S>, V>();
		reverseMap = new HashMap<V, WeightedObject<V, S>>();
	}

	/**
	 * @param orderMap
	 * @param reverseMap
	 */
	public MapBasedSortedSet(NavigableMap<WeightedObject<V, S>, V> orderMap,
			Map<V, WeightedObject<V, S>> reverseMap) {
		super();
		this.orderMap = orderMap;
		this.reverseMap = reverseMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.blackhole.utils.SortedSet#add(java.lang.Object,
	 * java.lang.Comparable)
	 */
	@Override
	public synchronized V add(V object, S score) {
		if (object == null || score == null) {
			throw new IllegalArgumentException(
					"object and weight should not be null");
		}
		V result = null;
		WeightedObject<V, S> key = new WeightedObject<V, S>(object, score);
		if (reverseMap.get(object) != null) {
			result = object;
			orderMap.remove(reverseMap.get(object));
		}
		orderMap.put(key, object);
		reverseMap.put(object, key);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.blackhole.utils.SortedSet#remove(java.lang.Object)
	 */
	@Override
	public synchronized V remove(V object) {
		if (object == null) {
			throw new IllegalArgumentException("object should not be null");
		}
		WeightedObject<V, S> weightedObject = reverseMap.get(object);
		if (weightedObject == null) {
			return null;
		}
		reverseMap.remove(object);
		return orderMap.remove(weightedObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.blackhole.utils.SortedSet#last()
	 */
	@Override
	public V last() {
		Entry<WeightedObject<V, S>, V> lastEntry = orderMap.lastEntry();
		if (lastEntry == null) {
			return null;
		}
		return lastEntry.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.blackhole.utils.SortedSet#first()
	 */
	@Override
	public V first() {
		Entry<WeightedObject<V, S>, V> firstEntry = orderMap.firstEntry();
		if (firstEntry == null) {
			return null;
		}
		return firstEntry.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.blackhole.utils.SortedSet#toList()
	 */
	@Override
	public synchronized List<V> toList() {
		return new ArrayList<V>(orderMap.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.blackhole.utils.SortedSet#first(us.codecraft.blackhole.utils
	 * .Selector)
	 */
	@Override
	public synchronized V first(Selector<V, S> selector) {
		for (Entry<WeightedObject<V, S>, V> entry : orderMap.entrySet()) {
			if (selector.select(entry.getValue(), entry.getKey().score)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.blackhole.utils.SortedSet#last(us.codecraft.blackhole.utils
	 * .Selector)
	 */
	@Override
	public synchronized V last(Selector<V, S> selector) {
		List<Entry<WeightedObject<V, S>, V>> list = new ArrayList<Entry<WeightedObject<V, S>, V>>(
				orderMap.entrySet());
		ListIterator<Entry<WeightedObject<V, S>, V>> listIterator = list
				.listIterator(list.size());
		while (listIterator.hasPrevious()) {
			Entry<WeightedObject<V, S>, V> entry = listIterator.previous();
			if (selector.select(entry.getValue(), entry.getKey().score)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.blackhole.utils.SortedSet#toList(us.codecraft.blackhole.
	 * utils.Selector)
	 */
	@Override
	public synchronized List<V> toList(Selector<V, S> selector) {
		List<V> list = new ArrayList<V>();
		for (Entry<WeightedObject<V, S>, V> entry : orderMap.entrySet()) {
			if (selector.select(entry.getValue(), entry.getKey().score)) {
				list.add(entry.getValue());
			}
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.blackhole.utils.SortedSet#select(us.codecraft.blackhole.
	 * utils.Selector)
	 */
	@Override
	public void select(Selector<V, S> selector) {
		for (Entry<WeightedObject<V, S>, V> entry : orderMap.entrySet()) {
			selector.select(entry.getValue(), entry.getKey().score);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.blackhole.utils.SortedSet#remove(us.codecraft.blackhole.
	 * utils.Selector)
	 */
	@Override
	public List<V> remove(Selector<V, S> selector) {
		List<V> list = new ArrayList<V>();
		Iterator<Entry<WeightedObject<V, S>, V>> iterator = orderMap.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<WeightedObject<V, S>, V> entry = iterator.next();
			if (selector.select(entry.getValue(), entry.getKey().score)) {
				list.add(entry.getValue());
				iterator.remove();
				reverseMap.remove(entry.getValue());
			}
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.codecraft.blackhole.utils.SortedSet#getScore(java.lang.Object)
	 */
	@Override
	public S getScore(V object) {
		WeightedObject<V, S> weightedObject = reverseMap.get(object);
		if (weightedObject == null) {
			return null;
		} else {
			return weightedObject.score;
		}
	}

}
