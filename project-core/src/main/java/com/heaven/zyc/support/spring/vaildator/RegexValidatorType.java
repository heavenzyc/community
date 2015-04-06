/**
 * @(#)ParamType.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.spring.vaildator;

import java.util.regex.Pattern;

/**
 * 基于正则表达式的验证类型
 * @author  jianguo.xu
 * @version 1.0,2011-6-11
 */
public enum RegexValidatorType{
	DIGIT("[-]{0,1}\\d+","{0}必须为数字"),
	DATE(Regex.dateRegex,"{0}日期格式不正确,必须为yyyy-mm-dd"),
	DATE_TIME(Regex.datetimeRegex,"{0}日期格式不正确,必须为yyyy-mm-dd hh:mm:ss"),
	TIME(Regex.timeRegex,"{0}时间格式不正确,必须为hh:mm:ss"),
	MOBILE("^1\\d{10}","{0}手机格式不正确"),
	IDENTITY("^\\d{15}|^\\d{18}$|^\\d{17}\\w$","{0}证件格式不正确,必须为15位数字或者18位数字及字母组成"),
	EMAIL(Regex.emailRegex,"{0}Email格式不正确");
	 
	private final String regex;	
	private final String errorTemplate;
	private RegexValidatorType(String regex,String errorTemplate) {
		this.regex = regex;
		this.errorTemplate = errorTemplate;
	}
	public String getRegex() {
		return regex;
	}
	public boolean validator(String input) {
		return Pattern.matches(regex, input);
	}
	public String getErrorMsg (String input) {
		return errorTemplate.replaceAll("\\{0\\}", input);
	}
	
	private static class Regex{
		static String dateRegex = "[1|2][0-9][0-9][0-9]-((0{0,1}[1-9])|(1[0-2]))-((0{0,1}[1-9])|([1|2][0-9])|(3[0-1]))";
		static String timeRegex = "((0{0,1}[0-9])|(1[0-9])|(2[0-3])):((0{0,1}[0-9])|([1-5][0-9])):((0{0,1}[0-9])|([1-5][0-9]))";
		static String datetimeRegex=dateRegex+" "+timeRegex;
		static String emailRegex="^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9_\\-]))+\\.([a-zA-Z0-9]{2,4})$";
	}
}