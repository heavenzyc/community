/**
 * @(#)AopTargetUtils.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.reflect;

import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
/**
 * @author  xu.jianguo
 * @date  2012-12-22
 * description
 */

public class SpringAopTargetUtils {
	private static final Log LOG = LogFactory.getLog(SpringAopTargetUtils.class);
	/**
	 * 获取 目标对象
	 * @param proxy 代理对象
	 * @return 
	 * @throws Exception
	 */
	public static Object getTarget(Object proxy) {
		if(!AopUtils.isAopProxy(proxy)) {
			return proxy;
		}
		if(AopUtils.isJdkDynamicProxy(proxy)) {
			try {
				return getJdkDynamicProxyTargetObject(proxy);
			} catch (Exception e) {
				LOG.error("get jdk proxy target error.", e);
			}
		} else {
			try {
				return getCglibProxyTargetObject(proxy);
			} catch (Exception e) {
				LOG.error("get cglib proxy target error.", e);
			}
		}
		return null;   
	}
	private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
		Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();     
        return target;
	}
	private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
		Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy); 
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);      
        Object target = ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();       
        return target;
	}	
}
