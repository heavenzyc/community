/**
 * @(#)ReadWriteDataSourceDecision.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.datasourceproxy;
/**
 * @author  xu.jianguo
 * @date  2012-12-21
 * 读写数据源上下文<br/>
 * 内部封装了ThreadLocal绑定于一个事务中
 */
public class ReadWriteDataSourceContext {
	private  enum DataSourceType {
       WRITE, READ;
    }
    private static final ThreadLocal<DataSourceType> holder = new ThreadLocal<DataSourceType>();
    
    public static void registWrite() {
        holder.set(DataSourceType.WRITE);
    }
    
    public static void registRead() {
        holder.set(DataSourceType.READ);
    }
    
    public static void reset() {
        holder.set(null);
    }
    
    public static boolean isChoiceNone() {
        return null == holder.get(); 
    }
    
    public static boolean isChoiceWrite() {
        return DataSourceType.WRITE == holder.get();
    }
    
    public static boolean isChoiceRead() {
        return DataSourceType.READ == holder.get();
    }
}
