/**
 * @(#)JSONValidatorException.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.spring.vaildator.exception;
/**
 * @author  xu.jianguo
 * @date  2012-11-28
 * description
 */
public class JsonValidatorException extends ValidatorException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JsonValidatorException() {
		super();
		 
	}

	public JsonValidatorException(String name, String errorMessage) {
		super(name, errorMessage);
	 
	}

	public JsonValidatorException(String message, Throwable cause) {
		super(message, cause);
		 
	}

	public JsonValidatorException(String errorMessage) {
		super(errorMessage);
		 
	}

	public JsonValidatorException(Throwable cause) {
		super(cause);
		 
	}
	
}
