/**
 * @(#)PollStrategy.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.datasourceproxy.route;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询策略
 * 
 * @author xu.jianguo
 * @date 2012-12-22 description
 */
public class RoundStrategy extends ReadDataSourceRouteStrategy {
	private AtomicInteger counter = new AtomicInteger(-1);
	public RoundStrategy(Map<String,DataSource> readDataSources) {
		super(readDataSources);
	}
	@Override
	public DataSourceEntry determineRoute() {
		if(counter.get()>=Integer.MAX_VALUE) {
			counter.getAndSet(-1);
		}
		int index = counter.incrementAndGet() % enableDataSourceEntryList.size();
		if (index < 0) {
			index = -index;
		}
		return enableDataSourceEntryList.get(index);		 
	}
}