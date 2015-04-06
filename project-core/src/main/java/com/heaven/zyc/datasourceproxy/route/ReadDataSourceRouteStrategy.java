/**
 * @(#)ReadDataSourceChoiceStrategy.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.datasourceproxy.route;

import com.heaven.zyc.datasourceproxy.DatasourceproxyException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 只读数据源路由选择策略
 * @author  xu.jianguo
 * @date  2012-12-22
 * description
 */

public abstract class ReadDataSourceRouteStrategy {
	private static final Log LOG = LogFactory.getLog(ReadDataSourceRouteStrategy.class);
	protected static List<DataSourceEntry> dataSourceEntryList = new ArrayList<DataSourceEntry>();
	/**
	 * 有效的 datasource集合<br/>
	 * 该集合会通过 @see {@link ReadDataSourceEnableCheck#scheduleCheck()}定期做健康检查<br/>
	 * 所以该集合可能会发生变化<br/>
	 */
	protected static List<DataSourceEntry> enableDataSourceEntryList = new ArrayList<DataSourceEntry>();
	public ReadDataSourceRouteStrategy(Map<String,DataSource> readDataSourceMap) {
		for(String name : readDataSourceMap.keySet()) {
			dataSourceEntryList.add(new DataSourceEntry(name, readDataSourceMap.get(name)));
		}
		ReadDataSourceEnableCheck.check();
		ReadDataSourceEnableCheck.scheduleCheck();
	}
	/**
	 * 决策  datasource 路由
	 * @return
	 * @author  xu.jianguo
	 */
	public DataSource determineDataSourceRoute() {
		DataSourceEntry dataSourceEntry =  determineRoute();
		if(LOG.isDebugEnabled()) {
			LOG.debug(" current determine read datasource:"+dataSourceEntry.getName());
		}
		return dataSourceEntry.getDataSource();
	}
	protected abstract DataSourceEntry determineRoute();
	
	 
	/**
	 * 定时健康检查作业调度
	 * @author xu.jianguo
	 *
	 */
	private static class ReadDataSourceEnableCheck {
		private static final ScheduledExecutorService executorService = 
				Executors.newSingleThreadScheduledExecutor();
		/**
		 * 定期健康检查
		 * 
		 * @author  xu.jianguo
		 */
		private static void scheduleCheck() {
			Runnable command = new Runnable() {
				@Override
				public void run() {
					check();
				}
			};
			executorService.scheduleWithFixedDelay(command, 1, 1,TimeUnit.MINUTES);
		}
		/**
		 * 健康检查逻辑
		 * 
		 * @author  xu.jianguo
		 */
		private static void check() {
			List<DataSourceEntry> enableDataSourceEntryList = new ArrayList<DataSourceEntry>();
			for(DataSourceEntry dataSourceEntry : dataSourceEntryList) {
				DataSource dataSource = dataSourceEntry.getDataSource();
				Connection conn = null;
				try {
					conn = dataSource.getConnection();
					if(conn != null)
						enableDataSourceEntryList.add(dataSourceEntry);
				} catch (SQLException e) {
					LOG.error("\r\nread datasource connection fail : "+dataSourceEntry.getName()+".\r\n"+e.getMessage());
				}
				finally {
					if(conn!=null) {
						try {
							conn.close();
						} catch (SQLException e) {
							LOG.error("close read datasource connection fail:"+dataSourceEntry.getName()+"."+e.getMessage());
						}
					}
				}
			}
			if(enableDataSourceEntryList.isEmpty()) {
				throw new DatasourceproxyException("all read dataSource fail.");
			}
			//mockDataSourceFail(enableDataSourceEntryList);
			ReadDataSourceRouteStrategy.enableDataSourceEntryList = enableDataSourceEntryList;
		}
		/**
		 * 模拟数据库宕掉
		 * @param enableDataSourceEntryList
		 * @author  xu.jianguo
		 */
		@SuppressWarnings("unused")
		private static void mockDataSourceFail(List<DataSourceEntry> enableDataSourceEntryList) {
			Random random = new Random();
			int index = random.nextInt(enableDataSourceEntryList.size());
			DataSourceEntry dataSourceEntry = enableDataSourceEntryList.remove(index);
			LOG.error("mock datasource fail in : "+dataSourceEntry.getName());
		}
	}
}
