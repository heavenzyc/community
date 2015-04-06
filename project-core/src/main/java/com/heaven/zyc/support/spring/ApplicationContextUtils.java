/**
 * @(#)ApplicationContextUtils.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author  xu.jianguo
 * @date  2012-12-19
 * description
 */
@Lazy(false)
@Component
public class ApplicationContextUtils implements ApplicationContextAware,Ordered,BeanPostProcessor {
	private static ApplicationContext applicationContext;
	@SuppressWarnings("static-access")
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	public static Object getService(String beanName) {
		return applicationContext.getBean(beanName);
	}
	public static <T> T getService(Class<T> type) {
		return applicationContext.getBean(type);
	}
	@Override
	public int getOrder() {
		return 0;
	}
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}
	 
}
