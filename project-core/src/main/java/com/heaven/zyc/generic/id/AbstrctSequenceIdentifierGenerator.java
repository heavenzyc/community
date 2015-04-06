/**
 * @(#)AbstrctSequenceIdentifierGenerator.java
 * Copyright 2013 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.generic.id;

import com.heaven.zyc.utils.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.Type;
import org.hibernate.util.PropertiesHelper;

import java.io.Serializable;
import java.sql.*;
import java.util.Date;
import java.util.Properties;

/**
 * @author xu.jianguo
 * @date 2013-6-24 
 * 自定义主键生成器
 */
public abstract class AbstrctSequenceIdentifierGenerator implements
		IdentifierGenerator, Configurable {

	public static final String TABLE = "table";

	public static final String DEFAULT_TABLE = "common_primary_key";

	public static final String COLUMN = "column";

	public static final String DEFAULT_COLUMN = "primary_key";

	public static final String TARGET_NAME = "target_name";

	public static final String DEFAULT_TARGET_NAME = "target_table";

	public static final String TS_COLUMN = "ts_column";

	public static final String DEFAULT_TS_COLUMN = "ts";

	public static final String LENGTH = "length";

	public static final int DEFAULT_LENGTH = 6;

	public static final String PATCH = "0";
	/**
	 * 自定义主键表名
	 */
	protected String table;
	/**
	 * 存放主键值的字段
	 */
	protected String column;
	/**
	 * 存放时间戳的字段
	 */
	protected String tsColumn;
	/**
	 * 主键值对应的业务类型
	 */
	protected String targetName;
	/**
	 * 主键值对应的业务类型值
	 */
	protected String targetValue;
	/**
	 * 主键值的长度
	 */
	protected int length;
	/**
	 * 是否每天自动增长 ,true:表示按每天自动增长,false:表示一直自增长.
	 */
	protected boolean autoIncrementInPreDay;
	
	public static final String AUTO_INCREMENT_PREDAY_NAME="increment_pre_day";

	protected String querySql;

	protected String updateSql;

	protected String insertSql;

	public void configure(Type type, Properties properties, Dialect dialect)
			throws MappingException {
		table = PropertiesHelper.getString(TABLE, properties, DEFAULT_TABLE);
		column = PropertiesHelper.getString(COLUMN, properties, DEFAULT_COLUMN);
		tsColumn = PropertiesHelper.getString(TS_COLUMN, properties,
				DEFAULT_TS_COLUMN);
		targetName = PropertiesHelper.getString(TARGET_NAME, properties,
				DEFAULT_TARGET_NAME);
		targetValue = defineTargetValue();
		length = PropertiesHelper.getInt(LENGTH, properties, DEFAULT_LENGTH);
		autoIncrementInPreDay = PropertiesHelper.getBoolean(AUTO_INCREMENT_PREDAY_NAME, properties,true);
		builderInsertSql();
		builderQuerySql();
		builderUpdateSql();
	}

	private void builderInsertSql() {
		insertSql = "insert into " + table + " (" + column + "," + tsColumn
				+ "," + targetName + ") values(?,?,?)";
	}

	private void builderQuerySql() {
		querySql = "select " + column + "," + tsColumn + " from " + table
				+ " where " + targetName + " = ?";
	}

	private void builderUpdateSql() {
		updateSql = "update " + table + " set " + column + " = ?," + tsColumn
				+ " = ? where " + targetName + " = ?";
	}

	public int sequelize(SessionImplementor session) {
		Connection conn = null;
		PreparedStatement queryStmt = null;
		ResultSet rs = null;
		PreparedStatement updateStmt = null;
		int defaultTransactionLevel = Connection.TRANSACTION_REPEATABLE_READ;
		boolean defaultAutoCommit = true;
		try {
			conn = session.connection();
			defaultTransactionLevel = conn.getTransactionIsolation();
			defaultAutoCommit = conn.getAutoCommit();
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			conn.setAutoCommit(false);
			queryStmt = conn.prepareStatement(querySql);
			rs = executeQuery(queryStmt);
			int primaryKeyValue = 0;
			if (!rs.next()) {
				updateStmt = conn.prepareStatement(insertSql);
				primaryKeyValue = executeInsert(updateStmt);
			} else {
				updateStmt = conn.prepareStatement(updateSql);
				primaryKeyValue = executeUpdate(updateStmt, rs);
			}
			conn.commit();
			return primaryKeyValue;
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			if (queryStmt != null)
				try {
					queryStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (updateStmt != null)
				try {
					updateStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				try {
					conn.setTransactionIsolation(defaultTransactionLevel);
					conn.setAutoCommit(defaultAutoCommit);
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return 0;
	}

	private ResultSet executeQuery(PreparedStatement preStmt)
			throws SQLException {
		preStmt.setString(1, targetValue);
		return preStmt.executeQuery();

	}

	private int executeInsert(PreparedStatement preStmt) throws SQLException {
		int primaryKeyValue = 1;
		preStmt.setInt(1, primaryKeyValue);
		preStmt.setTimestamp(2, new Timestamp(new Date().getTime()));
		preStmt.setString(3, targetValue);
		preStmt.executeUpdate();
		return primaryKeyValue;
	}

	private int executeUpdate(PreparedStatement preStmt, ResultSet rs)
			throws SQLException {
		int primaryKeyValue = rs.getInt(1);
		Date ts = rs.getTimestamp(2);
		Date now = new Date();
		if (!autoIncrementInPreDay) {
			primaryKeyValue++;
		} else {
			primaryKeyValue = (DateUtils.subtractDay(now, ts) >= 1) ? 1
					: (primaryKeyValue + 1);
		}
		preStmt.setInt(1, primaryKeyValue);
		preStmt.setTimestamp(2, new Timestamp(new Date().getTime()));
		preStmt.setString(3, targetValue);
		preStmt.executeUpdate();
		return primaryKeyValue;
	}

	public String converse(int iPrimaryKey) {
		String sPrimaryKey = String.valueOf(iPrimaryKey);
		int primaryKeyLength = sPrimaryKey.length();
		if (primaryKeyLength >= length)
			return sPrimaryKey.substring(primaryKeyLength - length);
		else {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < length - primaryKeyLength; i++)
				buffer.append(PATCH);
			buffer.append(sPrimaryKey);
			return buffer.toString();
		}
	}

	public synchronized Serializable generate(SessionImplementor session,
			Object obj) throws HibernateException {
		int iPrimaryKey = sequelize(session);
		String sPrimaryKey = converse(iPrimaryKey);
		return assemble(sPrimaryKey);
	}
	/**
	 * 子类根据主键值再次封装、装配主键，<br/>
	 * eg.date+primaryKey<br/>
	 * @return
	 * @author  xu.jianguo
	 */
	public abstract String assemble(String primaryKey);
	/**
	 * 子类定义主键值对应的业务类型值
	 * @return
	 * @author  xu.jianguo
	 */
	public abstract String defineTargetValue();
}
