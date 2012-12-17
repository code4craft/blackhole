package us.codecraft.blackhole.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/**
 * @author yihua.huang@dianping.com
 * @date Dec 15, 2012
 */
public class SpringLocator {

	public static ApplicationContext applicationContext;

	public static Object getBean(String name) throws BeansException {
		return applicationContext.getBean(name);
	}

	public static <T> T getBean(String name, Class<T> requiredType)
			throws BeansException {
		return applicationContext.getBean(name, requiredType);
	}

	public static <T> T getBean(Class<T> requiredType) throws BeansException {
		return applicationContext.getBean(requiredType);
	}

	public static Object getBean(String name, Object... args)
			throws BeansException {
		return applicationContext.getBean(name, args);
	}

	public static boolean containsBean(String name) {
		return applicationContext.containsBean(name);
	}

	public static boolean isSingleton(String name)
			throws NoSuchBeanDefinitionException {
		return applicationContext.isSingleton(name);
	}

	public static boolean isPrototype(String name)
			throws NoSuchBeanDefinitionException {
		return applicationContext.isPrototype(name);
	}

	public static boolean isTypeMatch(String name, Class<?> targetType)
			throws NoSuchBeanDefinitionException {
		return applicationContext.isTypeMatch(name, targetType);
	}

	public static Class<?> getType(String name)
			throws NoSuchBeanDefinitionException {
		return applicationContext.getType(name);
	}

	public static String[] getAliases(String name) {
		return applicationContext.getAliases(name);
	}

}
