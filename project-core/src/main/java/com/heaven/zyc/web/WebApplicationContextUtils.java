/**
 * @(#)WebApplicationContextUtils.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.web;

import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;

/**
 * 通过  ServletContext 获得 spring 容器以及容器中定义的bean
 * @author  jianguo.xu
 * @version 1.0,2011-2-17
 */
public class WebApplicationContextUtils {
	public static ApplicationContext getContext(ServletContext sc) {
		return org.springframework.web.context.support.WebApplicationContextUtils
				.getWebApplicationContext(sc);
	}
	
	public static Object getService(String beanName, ServletContext sc) {
		return getContext(sc).getBean(beanName);
	}
}
