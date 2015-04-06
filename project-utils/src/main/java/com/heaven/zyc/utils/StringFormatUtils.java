/**
 * @(#)StringFormat.java
 * Copyright 2013 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.utils;
 
/**
 * @author  xu.jianguo
 * @date  2013-9-4
 * description
 */
 

public class StringFormatUtils {

	/**
	 * 格式化字符串<br/>
	 * eg. abc{0}efg{1}
	 * @param template
	 * @param args
	 * @return
	 * @author  xu.jianguo
	 */
	public static String format(String template, String... args) {
		for (int i = 0; i < args.length; i++) {
			template = template.replaceAll("\\{\\s*" + i + "\\s*\\}", args[i]);
		}
		return template;
	}
	/**
	 * 安全链接字符串
	 * 
	 * @param strs
	 * @return
	 */
	public static String softLink(String... strs) {
		StringBuffer sb = new StringBuffer();
		for (String s : strs) {
			sb.append(StringUtils.isNullOrEmpty(s) ? "" : s);
		}
		return sb.toString();
	}
}