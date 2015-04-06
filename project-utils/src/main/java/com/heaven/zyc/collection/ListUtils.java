/**
 * @(#)CollectionUtils.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.collection;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

/**
 * 集合工具类
 * @author  jianguo.xu
 * @version 1.0,2011-11-14
 */
public class ListUtils {
	/**
	 * 传入的集合拆分多个小集合
	 * @author jianguo.xu
	 * @param <T>
	 * @param collection 	集合
	 * @param maxCount		每个集合的最大记录数
	 * @return
	 */
	public  static<T>  List<List<T>> splitListByCount(List<T> list,int maxCount) {
		List<List<T>> collectioin = new ArrayList<List<T>>();
		int listSize = list.size();
		int splitCount = listSize%maxCount>0?((listSize/maxCount)+1):(listSize/maxCount);
		for(int i = 0;i<splitCount;i++) {
			int fromIndex = i*maxCount;
			int toIndex = (i+1)*maxCount>=listSize?listSize:(i+1)*maxCount;
			List<T> subList = list.subList(fromIndex, toIndex);
			collectioin.add(subList);
		}
		return collectioin;
	}
	/**
	 * 集合对象的复制<br/>
	 * 需要保证集合中对象有默认的构造方法<br/>
	 * @param <T>
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  static<T>  List<T> clone(List<T> list) {
		List<T> collectioin = new ArrayList<T>();
		for(T obj : list) {
			T temp;
			try {
				temp = (T) obj.getClass().newInstance();
			} catch (Exception e) {
				throw new RuntimeException("clone list error.", e);
			}
			try {
				BeanUtils.copyProperties(obj.getClass().newInstance(), obj);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("copy object properties error.", e);
				 
			}
			collectioin.add(temp);
		}
		return collectioin;
	}

}
