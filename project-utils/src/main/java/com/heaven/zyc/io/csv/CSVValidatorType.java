/**
 * @(#)ParamType.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.io.csv;

import java.util.regex.Pattern;

/**
 * CSV文件验证类型
 * @author  jianguo.xu
 * @version 1.0,2011-6-11
 */
public enum CSVValidatorType {
	DIGIT("[-]{0,1}\\d+","{0}必须为数字"),
	DATE(Regex.dateRegex,"{0}日期格式不正确,必须为yyyy-mm-dd"),
	DATE_TIME(Regex.datetimeRegex,"{0}日期格式不正确,必须为yyyy-mm-dd hh:mm:ss"),
	TIME(Regex.timeRegex,"{0}时间格式不正确,必须为hh:mm:ss"),
	MOBILE("^1\\d{10}","{0}手机格式不正确"),
	LEVEL("^普通会员|银卡会员|金卡会员|钻石会员$","会员等级必须为普通会员、银卡会员、金卡会员、钻石会员"),
	IDENTITY("^\\d{15}|^\\d{18}$|^\\d{17}\\w$","{0}证件格式不正确,必须为15位数字或者18位数字及字母组成"),
	EMAIL(Regex.emailRegex,"{0}Email格式不正确");
	private final String regex;	
	private final String errorTemplate;
	private CSVValidatorType(String regex,String errorTemplate) {
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
	/*
	public static void main(String[] args) {
		String dateRegex = "[1|2][0-9][0-9][0-9]-((0{0,1}[1-9])|(1[0-2]))-((0{0,1}[1-9])|([1|2][0-9])|(3[0-1]))";
		
		String value="1999-12-31";
		boolean p= Pattern.matches(dateRegex, value);
		System.out.println(p);
		
		String tRegex = "((0{0,1}[0-9])|(1[0-9])|(2[0-3])):((0{0,1}[0-9])|([1-5][0-9])):((0{0,1}[0-9])|([1-5][0-9]))";

		String time="0:0:0";
		boolean o= Pattern.matches(tRegex, time);
		System.out.println(o);
		
		String r1="^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9_\\-]))+\\.([a-zA-Z0-9]{2,4})$";
		String v1="tig.er-0_29@t_o-m.com";
		boolean b1= Pattern.matches(r1, v1);
		System.out.println(b1);
		
	}*/
	 
}

 
