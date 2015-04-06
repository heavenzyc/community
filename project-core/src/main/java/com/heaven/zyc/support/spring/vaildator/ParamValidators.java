/**
 * @(#)Customer.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.spring.vaildator;

import java.lang.annotation.*;

/**
 * 验证注解
 * @author  jianguo.xu
 * @version 1.0,2011-4-27
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamValidators {
	ResultType resultType() default ResultType.MODE;
	ParamValidator[] value();
}





