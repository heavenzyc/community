/**
 * @(#)DataSourceProxy.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.datasourceproxy;

import com.heaven.zyc.datasourceproxy.route.ReadDataSourceRouteStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 读/写动态选择数据库实现<br/>
 * 实现了一写库多读库选择功能<>
 * @author xu.jianguo
 * @date 2012-12-21 description
 */
public class DataSourceProxy extends AbstractDataSource implements
		InitializingBean {
	private static final Log log = LogFactory.getLog(DataSourceProxy.class);
	private ReadDataSourceRouteStrategy readDataSourceRouteStrategy;
	private DataSource writeDataSource;
	/**
	 * 设置读库的路由策略模式
	 * @param readDataSourceRouteStrategy
	 * @author  xu.jianguo
	 */
	public void setReadDataSourceRouteStrategy(
			ReadDataSourceRouteStrategy readDataSourceRouteStrategy) {
		this.readDataSourceRouteStrategy = readDataSourceRouteStrategy;
	}
	/**
	 * 设置写库
	 * @param writeDataSource
	 * @author  xu.jianguo
	 */
	public void setWriteDataSource(DataSource writeDataSource) {
		this.writeDataSource = writeDataSource;
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		if (writeDataSource == null) {
			throw new DatasourceproxyException("property 'writeDataSource' is required");
		}
		if (readDataSourceRouteStrategy == null) {
			throw new DatasourceproxyException("property 'readDataSourceRouteStrategy' is required");
		}
	}
	/**
	 * 选择数据源<br/>
	 * 根据当前上下文的数据源的读写模式来选择读库还是写库<br/>
	 * 如果上下文中为注册读写模式则选择写库<br/>
	 * 如果是读模式则根据读库的路由模式来选择读数据源
	 * @return
	 * @author  xu.jianguo
	 */
	private DataSource determineDataSource() {
		if (ReadWriteDataSourceContext.isChoiceWrite()) {
			log.debug("current determine write datasource");
			return writeDataSource;
		}
		if (ReadWriteDataSourceContext.isChoiceNone()) {
			log.debug("no choice read/write, default determine write datasource");
			return writeDataSource;
		}
		return readDataSourceRouteStrategy.determineDataSourceRoute();
	}
	@Override
	public Connection getConnection() throws SQLException {
		return determineDataSource().getConnection();
	}
	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return determineDataSource().getConnection(username, password);
	}
}