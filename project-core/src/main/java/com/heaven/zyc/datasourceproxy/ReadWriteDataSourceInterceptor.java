/**
 * @(#)ReadWriteDataSourceProcessor.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.datasourceproxy;

import com.heaven.zyc.reflect.ClassUtils;
import com.heaven.zyc.reflect.SpringAopTargetUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.Pointcuts;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author  xu.jianguo
 * @date  2012-12-21
 * 读写分离拦截器<br/>
 * 根据@Transactional 的 readOnly属性来判断写操作还是读操作
 */
public class ReadWriteDataSourceInterceptor implements BeanPostProcessor,Ordered, MethodInterceptor{
	private static final Log log = LogFactory.getLog(ReadWriteDataSourceInterceptor.class);
	public static  String  default_transactionExpression = "execution(* *.service..*.*(..))";
	/**
	 * 是否强制检测@transaction的 readOnly配置<br/>
	 * 如果需要检查，那么transaction中的propagation属性如果为[SUPPORT,NOT_SUPPORTED,NEVER]<BR/>
	 * 则readOnly必须为true否则抛出异常<br/>
	 * 如果不需要检查那么会以警告的方式提示!<br/>
	 * 默认值为true
	 */
	private boolean forceCheckReadOnly =true;
	private AspectJExpressionPointcut pointcut;	
	private Set<Method> readMethdSet = new HashSet<Method>();
	
	public ReadWriteDataSourceInterceptor() {
		pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(default_transactionExpression);
	}
	/**
	 * 是否强制检测@transaction的 readOnly配置<br/>
	 * 如果需要检查，那么transaction中的propagation属性如果为[SUPPORT,NOT_SUPPORTED,NEVER]<BR/>
	 * 则readOnly必须为true否则抛出异常<br/>
	 * 如果不需要检查那么会以警告的方式提示!<br/>
	 * 默认值为true
	 */
	public void setForceCheckReadOnly(boolean forceCheckReadOnly) {
		this.forceCheckReadOnly = forceCheckReadOnly;
	}

	/**
	 * 设置读写分离处理器要拦截的方法 pointcut(切入点)表达式<br/>
	 * 默认表达式是 {@link #default_transactionExpression}
	 * @param transactionExpression
	 * @author  xu.jianguo
	 */
	public void setTransactionExpression(String transactionExpression) {
		pointcut.setExpression(transactionExpression);
	}

	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		 return bean;
	}
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		putReadMethod(bean);
		 return bean;
	}
	
	private void putReadMethod(Object bean) {
		Object realBean = SpringAopTargetUtils.getTarget(bean);
		Class<?> targetClass = realBean.getClass();
		Method[] methods = targetClass.getMethods();
		for(Method method : methods) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			boolean match = Pointcuts.matches(pointcut, method, targetClass, parameterTypes);
			if(!match)continue;
			Transactional transactional = method.getAnnotation(Transactional.class);
			if(transactional==null) continue;
			if(transactional.readOnly()) {
				readMethdSet.add(method);
				continue;
			}
			if(!transactional.readOnly()&&(transactional.propagation() == Propagation.SUPPORTS||
					transactional.propagation() == Propagation.NOT_SUPPORTED
					||transactional.propagation() == Propagation.NEVER)) {
				if(forceCheckReadOnly) {
					throw new DatasourceproxyException(method.toString()+" transaction annotation readonly property must is 'true'"); 
				}
				else {
					log.warn("\r\nWARNING........\r\n"+method.toString()+"\r\ntransaction annotation readonly property should be is 'true'");
				}
			}
		}
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object invocationObj = SpringAopTargetUtils.getTarget(invocation.getThis());
		Class<?> clazz = ClassUtils.getRealClass(invocationObj);
		Method invocationMethod = invocation.getMethod();
		Method method = clazz.getMethod(invocationMethod.getName(), invocationMethod.getParameterTypes());
		registDataSourceType(method);
		try {
			return invocation.proceed();
		}
		finally {
			if(ReadWriteDataSourceContext.isChoiceWrite()) {
				ReadWriteDataSourceContext.reset();
			}
		}
	}
	/**
	 *注册数据源的读写模式<br/>
	 *如果在当前上下文中的为写模式，那么一直是写模式
	 * @param method
	 * @author  xu.jianguo
	 */
	private void registDataSourceType(Method method) {
		if(ReadWriteDataSourceContext.isChoiceWrite())return;
		if(readMethdSet.contains(method)) {
			ReadWriteDataSourceContext.registRead();
		}
		else {
			ReadWriteDataSourceContext.registWrite();
		}
	}
}