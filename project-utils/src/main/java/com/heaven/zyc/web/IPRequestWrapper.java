package com.heaven.zyc.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * IP包装器，用于封装获取到真实IP
 * @author yangfan 2013-10-15 下午2:51:08
 */
public class IPRequestWrapper extends HttpServletRequestWrapper {

	private static final String WRAPPED_BY_IPRequestWrapper = "WRAPPED_BY_IPRequestWrapper";

	public IPRequestWrapper(HttpServletRequest request) {
		super(request);
		request.setAttribute(WRAPPED_BY_IPRequestWrapper, true);
	}

	public String getRemoteAddr() {
		HttpServletRequest request = (HttpServletRequest) this.getRequest();
		String ip = IPUtil.getRemoteAddr(request);
		return ip;
	}
}
