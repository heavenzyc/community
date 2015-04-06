/**
 * @(#)Customer.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.spring.vaildator;

import java.lang.annotation.*;

/**
 *验证注解
 * @author  jianguo.xu
 * @version 1.0,2011-4-27
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamValidator {
	
	/**
	 * 请求参数
	 * @author jianguo.xu
	 * @return
	 */
	String param();
	/**
	 * 请求参数对应的注释名字
	 * @author jianguo.xu
	 * @return
	 */
	String paramName();
	/**
	 * 验证类型
	 * @author jianguo.xu
	 * @return
	 */
	RegexValidatorType[] vaildatorTypes()default{};
	 
	/**
	 * 是否是必填，默认为false
	 * @author jianguo.xu
	 * @return
	 */
	boolean required() default false;
	
	/**
	 * 验证长度
	 * 如果只有一个整数则表示参数的长度和该整数要一样长
	 * @author jianguo.xu
	 * @return
	 */
	int[] length()default {};
	/**
	 * 必须是一个数字，其值必须大于等于指定的最小值 
	 * @return
	 */
	long min() default Long.MIN_VALUE;
	/**
	 * 必须是一个数字，其值必须小于等于指定的最大值
	 * @return
	 */
	long max() default Long.MAX_VALUE;
	/**
	 * 
	 *  必须是一个数字(任意精度的有符号十进制数)，其值必须大于等于指定的最小值 
	 * @return
	 */
	String DecimalMin() default "";
	/**
	 * 
	 *  必须是一个数字(任意精度的有符号十进制数)，其值必须小于等于指定的最大值
	 * @return
	 */
	String DecimalMax() default "";
	/**
	 * 自定义的正则验证表达式
	 * @return
	 * @author  xu.jianguo
	 */
	String customRegex() default "";
	/**
	 * 自定义的正则验证表达式校验错误返回的消息内容哦
	 * @return
	 * @author  xu.jianguo
	 */
	String customErrorMsg() default "校验失败";
}