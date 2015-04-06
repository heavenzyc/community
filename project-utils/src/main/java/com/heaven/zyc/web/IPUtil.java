package com.heaven.zyc.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IP工具<br/>
 * FIXME 存在伪造IP的漏洞,X-Forwarded-For该字段名称需可以自定义
 * @author yangfan 2013-10-15 下午2:37:14
 */
public class IPUtil {

	private static final String HAS_GET_REMOTE_ADDR_BY_IPUtil = "HAS_GET_REMOTE_ADDR_BY_IPUtil";

	private static final Logger log = LoggerFactory.getLogger(IPUtil.class);

	/**
	 * 获取IP地址
	 * <br/>主要解决经过nginx等代理后，无法获取IP的问题
	 * @param request
	 * @return
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
//		//首先进行判断,避免死循环
//		if(request.getAttribute(HAS_GET_REMOTE_ADDR_BY_IPUtil) != null) {
//			return request.getRemoteAddr();
//		}

		String[] headers = new String[] {"X-Forwarded-For","Proxy-Client-IP","WL-Proxy-Client-IP","HTTP_X_FORWARDED_FOR","X-Real-IP"};
		String ip = tryGetIP(request,headers);
		if(ip == null) {
			ip = request.getRemoteAddr();
			log.debug("no ip found in request headers, then using default request.getRemoteAddr() method, ip=" + ip);
		}
		request.setAttribute(HAS_GET_REMOTE_ADDR_BY_IPUtil, true);

		return ip;
	}

	private static String tryGetIP(HttpServletRequest request, String[] headers) {
		for (String header : headers) {
			String value = request.getHeader(header);
			if(value != null && value.trim().length() > 0) {
				String ip = value.split("\\,")[0];
				log.debug("found ip using header: " + header + ", ip=" + ip);
				return ip;
			}
		}
		return null;
	}
}
