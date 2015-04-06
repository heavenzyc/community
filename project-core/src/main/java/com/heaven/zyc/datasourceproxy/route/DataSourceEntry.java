/**
 * @(#)DataSourceEntry.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.datasourceproxy.route;

import javax.sql.DataSource;

/**
 * @author  xu.jianguo
 * @date  2012-12-23
 * description
 */
public class DataSourceEntry {
	/**
	 * 权重
	 */
	 private int weighte=1;
	/**
	 * datasource 名称
	 */
	private String name;
	/**
	 * dataSource
	 */
	private DataSource dataSource;
	public DataSourceEntry(String name, DataSource dataSource) {
		super();
		this.name = name;
		this.dataSource = dataSource;
	}
	public String getName() {
		return name;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataSourceEntry other = (DataSourceEntry) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	public int getWeighte() {
		return weighte;
	}
	public void setWeighte(int weighte) {
		this.weighte = weighte;
	}
	
	
}
