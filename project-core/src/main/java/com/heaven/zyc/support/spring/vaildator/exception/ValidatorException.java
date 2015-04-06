/**
 * @(#)VaildatorException.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.spring.vaildator.exception;
/**
 * description
 * @author  jianguo.xu
 * @version 1.0,2011-6-11
 */
public class ValidatorException extends RuntimeException{
	
	private String name;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ValidatorException() {
		super();
		 
	}

	public ValidatorException(String message, Throwable cause) {
		super(message, cause);
		 
	}
	public ValidatorException(String errorMessage) {
		super(errorMessage);
	}
	public ValidatorException(String name,String errorMessage) {
		super(errorMessage);
		this.name = name; 
	}

	public ValidatorException(Throwable cause) {
		super(cause);
		 
	}
	

}
