/**
 * @(#)CSVValidator.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.io.csv;



/**
 * CVS文件验证规则
 * @author  jianguo.xu
 * @version 1.0,2011-10-22
 */
public class CSVValidator {
	/**
	 * CSV需要验证列的索引<br/>
	 * 索引从0开始
	 */
	private final int index;
	/**
	 * 是否必填验证
	 */
	private boolean required;
	/**
	 * 长度验证<br/>
	 * 如果数组大小=1 则为固定长度<br/>
	 * 如果数组大小=2 则长度为两个值之间<br/>
	 */
	private int[] len;
	/**
	 * 最小值验证<br/>
	 * 为空时，不需要验证
	 */
	private Integer minValue;
	/**
	 * 最大值验证<br/>
	 * 为空时，不需要验证
	 */
	private Integer maxValue;
	private CSVValidatorType[] vaildatorTypes;
	public CSVValidator(int index) {
		super();
		this.index = index;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public int[] getLen() {
		return len;
	}
	public void setLen(int[] len) {
		this.len = len;
	}
	
	public CSVValidatorType[] getVaildatorTypes() {
		return vaildatorTypes;
	}
	public void setVaildatorTypes(CSVValidatorType[] vaildatorTypes) {
		this.vaildatorTypes = vaildatorTypes;
	}
	public int getIndex() {
		return index;
	}
	public Integer getMinValue() {
		return minValue;
	}
	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}
	public Integer getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}
	
}
