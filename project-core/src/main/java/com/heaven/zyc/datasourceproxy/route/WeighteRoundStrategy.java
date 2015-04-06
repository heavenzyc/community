/**
 * @(#)PercentRandomStrategy.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.datasourceproxy.route;

import com.heaven.zyc.datasourceproxy.DatasourceproxyException;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 按权重轮询路由规则
 * @author  xu.jianguo
 * @date  2012-12-22
 * description
 */
public class WeighteRoundStrategy extends ReadDataSourceRouteStrategy {
	private AtomicInteger counter = new AtomicInteger(-1);
	/**
	 * 有效的读库数据源集合副本
	 */
	private List<DataSourceEntry> cloneEnableDataSourceEntryList;
	/**
	 * 根据权重比例计算出的数据库列表<br/>
	 * 如：两个数据库权重比例是 A:2,B:1,C:3,
	 * weighteDataSourceEntrys =[A,A,B,C,C,C]
	 */
	private DataSourceEntry[] weighteDataSourceEntrys;
	public WeighteRoundStrategy(Map<String, DataSource> readDataSourceMap) {
		super(readDataSourceMap);
		init(null);
	}
	
	public WeighteRoundStrategy(Map<String, DataSource> readDataSourceMap,int[] weightes) {
		super(readDataSourceMap);
		init(weightes);
	}
	
	private void init(int[] weightes) {
		initDataSourceWeight(weightes);
		calculateWeighte();
	}
	/**
	 * 由于enableDataSourceEntryList可能会发生变化，<br/>
	 * 所以当enableDataSourceEntryList发生变化后再次计算权重<br/>
	 * 如果未发生变化不做任何操作
	 * 
	 * @author  xu.jianguo
	 */
	private void calculateWeighte() {
		if(enableDataSourceEntryList.equals(cloneEnableDataSourceEntryList))
			return;
		cloneEnableDataSourceEntryList = new ArrayList<DataSourceEntry>();
		cloneEnableDataSourceEntryList.addAll(enableDataSourceEntryList);
		initWeighteDataSourceEntryList();
	}
	private void initWeighteDataSourceEntryList() {
		int count = 0; 
		for(DataSourceEntry item:cloneEnableDataSourceEntryList) {
			int weight = item.getWeighte();
			if(weight<1||weight>10) {
				throw new DatasourceproxyException("datasource weight must >0 & <=10");
			}
			count+=weight;
		}
		weighteDataSourceEntrys = new DataSourceEntry[count];
		for(int i = 0,fillIndex = 0;i<cloneEnableDataSourceEntryList.size();i++) {
			DataSourceEntry item = cloneEnableDataSourceEntryList.get(i);
			Arrays.fill(weighteDataSourceEntrys, fillIndex, fillIndex+item.getWeighte(), item);
			fillIndex+=item.getWeighte();
		}
	}
	
	
	private void initDataSourceWeight(int[] weightes) {
		if(weightes==null) {
			weightes = new int[dataSourceEntryList.size()];
			Arrays.fill(weightes, 1);
		}
		if(dataSourceEntryList.size() !=weightes.length) {
			throw new DatasourceproxyException("readDataSources.size  must same with weightes.length");
		}
		for(int i = 0;i<weightes.length;i++) {
			DataSourceEntry dataSourceEntry = dataSourceEntryList.get(i);
			dataSourceEntry.setWeighte(weightes[i]);
		}
	}
	 
	@Override
	public DataSourceEntry determineRoute() {
		calculateWeighte();
		if(counter.get()>=Integer.MAX_VALUE) {
			counter.getAndSet(-1);
		}
		int index = counter.incrementAndGet() % weighteDataSourceEntrys.length;
		if (index < 0) {
			index = -index;
		}
		return weighteDataSourceEntrys[index];	 
	}
}
