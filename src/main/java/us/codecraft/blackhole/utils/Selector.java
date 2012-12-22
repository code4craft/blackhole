package us.codecraft.blackhole.utils;

/**
 * 
 * 
 * @author yihua.huang
 * 
 */
public interface Selector<V, S> {

	boolean select(V object, S score);
}
