/**
 * @(#)ObjectConvertInterceptor.java
 *
 * Copyright 2011 JUST IN MOBILE, Inc. All rights reserved.
 */
package com.heaven.zyc.support.spring.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;


/**
 * description
 * @author  jianguo.xu
 * @version 1.0,2011-4-27
 */
public class CustomObjectConvertResolver implements WebArgumentResolver{
	@Autowired(required=false)
	private CustomObjectConvertHandler[] customObjectConvertHandlers;
	 
	@Override
	public Object resolveArgument(MethodParameter methodParameter,
			NativeWebRequest webRequest) throws Exception {
		Object[] paramAnnos = methodParameter.getParameterAnnotations();
		if(paramAnnos == null||paramAnnos.length == 0) return UNRESOLVED;
		for(Object anno : paramAnnos) {
			if(!ObjectConvertAnno.class.isInstance(anno))continue;
			ObjectConvertAnno objectConvertAnno = (ObjectConvertAnno) anno;
			Object object = execute( methodParameter, webRequest);
			if(objectConvertAnno.required()&&object == null) {
				throw new IllegalStateException("Missing parameter '" + methodParameter.getParameterName() + "' of type [" + methodParameter.getParameterType().getName()+ "]");
			}
			return object;
		}
		return UNRESOLVED;
	}
	
	private Object execute(MethodParameter methodParameter,NativeWebRequest webRequest) {
		Class<?> paramType = methodParameter.getParameterType();
		if(customObjectConvertHandlers == null) return null;
		for(CustomObjectConvertHandler handler : customObjectConvertHandlers) {
			if(handler.getObjectType().equals(paramType)) {
				return handler.execute(methodParameter, webRequest);
			}
		}
		throw new RuntimeException("CustomObjectConvertHandler object is not fund");
	}	
}
