/**
 * @(#)DatasourceproxyException.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.datasourceproxy;
/**
 * @author  xu.jianguo
 * @date  2012-12-23
 * description
 */
public class DatasourceproxyException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatasourceproxyException() {
		super();
	}

	public DatasourceproxyException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public DatasourceproxyException(String s) {
		super(s);
	}

	public DatasourceproxyException(Throwable throwable) {
		super(throwable);
	}
	
}
