/**
 * @(#)Customer.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.spring.interceptor;

import java.lang.annotation.*;

/**
 * Spring中匹配controller中参数的注解
 * @author  jianguo.xu
 * @version 1.0,2011-4-27
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ObjectConvertAnno {

	/**
	 * 指定该参数的值是否允许为 null
	 * 默认是不允许
	 * Whether the parameter is required.
	 * <p>Default is <code>true</code>, leading to an exception thrown in case
	 * of the parameter missing in the request. Switch this to <code>false</code>
	 * if you prefer a <code>null</value> in case of the parameter missing.
	 */
	boolean required() default true;
	
	
}





