/**
 * @(#)MySQLExtendDialect.java
 * Copyright 2013 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.config;

import org.hibernate.Hibernate;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.function.StandardSQLFunction;

/**
 * @author  xu.jianguo
 * @date  2013-4-17
 * description
 */
public class MySQLExtendDialect extends MySQLDialect {

	@SuppressWarnings("deprecation")
	public MySQLExtendDialect() {
		super();
		registerFunction("dis", new StandardSQLFunction("dis", Hibernate.DOUBLE));

	}
	
}
