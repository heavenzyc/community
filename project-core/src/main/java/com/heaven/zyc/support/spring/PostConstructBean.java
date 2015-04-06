/**
 * @(#)PostConstructBean.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.spring;

import com.heaven.zyc.reflect.ClassUtils;
import com.heaven.zyc.support.spring.anno.InitMethod;
import com.heaven.zyc.utils.ConfigUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * spring启动后初始化操作
 * @author  jianguo.xu
 * @version 1.0,2011-4-28
 */
 
@Lazy(false)
@Service
public class PostConstructBean{
	private static final Log LOG = LogFactory.getLog(PostConstructBean.class);
	 
	@PostConstruct 
	public void afterPropertiesSet() throws Exception {
		List<Class<?>> classes = ClassUtils.getClassPathClasses(ConfigUtils.BASE_PACKAGE);
		for(Class<?> clazz : classes) {
			processInitMethod(clazz);
		}
	}
	
	private void processInitMethod(Class<?> clazz) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method[] methods = clazz.getDeclaredMethods();
		for(Method method : methods) {
			if(method.getParameterTypes().length>0) continue;
			InitMethod initAnno = method.getAnnotation(InitMethod.class);
			if(initAnno == null)continue;
			Object bean = initAnno.isStatic()?null:ClassUtils.instanceObject(clazz);
			LOG.info("init method : "+clazz.getName()+"."+method.getName());
			method.invoke(bean, null);
		}
	}
}
