/**
 * @(#)FilterUtils.java
 * Copyright 2013 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.web.filter;

import org.springframework.util.AntPathMatcher;

/**
 * @author  xu.jianguo
 * @date  2013-8-19
 * 过滤器工具类
 */
public class FilterUtils {
	
	private static final String[] filterEndUrls= new String[]{".jsp",".json",".htm","/"};
	
	private static final AntPathMatcher urlMatcher = new AntPathMatcher();
	/**
	 * 判断是否拦截指定的URL<br/>
	 * 对于js,css,html,图片等静态资源将不予拦截
	 * @param url		判断是否拦截的URL
	 * @return
	 * @author  xu.jianguo
	 */
	public static boolean filterUrl(String url) {
		return filterUrl(url, null);
	}
	/**
	 * 判断是否拦截指定的URL<br/>
	 * 对于js,css,html,图片等静态资源将不予拦截
	 * @param url		判断是否拦截的URL
	 * @param excludeUrls	忽略的URL集合,该集合可以是 Ant-style path patterns
	 * @return
	 * @author  xu.jianguo
	 */
	public static boolean filterUrl(String url,String[] excludeUrls) {
		for(String endUrl : filterEndUrls) {
			if(url.endsWith(endUrl))return true;
		}
		if (exclude(url,excludeUrls))
			return false;
		return matchExistPoint(url);
	}
	/**
	 * 判断是否忽略该URL
	 * @param url
	 * @param excludeUrls
	 * @return
	 * @author  xu.jianguo
	 */
	private static boolean exclude(String url,String[] excludeUrls) {
		if(excludeUrls == null||excludeUrls.length ==0) 
			return false;
		for (int i = 0; i < excludeUrls.length; i++) {
			if (urlMatcher.match(excludeUrls[i].toLowerCase(),
					url.toLowerCase()))
				return true;
		}
		return false;	
	}
	/**
	 * 判断是否带有小数点后缀
	 * @return
	 * @author  xu.jianguo
	 */
	private static boolean matchExistPoint(String url) {
		String[] temp = url.split("/");
		if (temp == null || temp.length == 0) {
			return false;
		}
		return temp[temp.length - 1].matches("[^\\.]*$") ? true : false;
	}
}
