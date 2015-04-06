package com.heaven.zyc.support.spring.vaildator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author  xu.jianguo
 * @date  2012-10-21
 * 基于model绑定的验证结果
 */
public class ValidatorResult{
	private Map<String, String> errorModel = new HashMap<String, String>();
	/**
	 * 验证是否错误
	 * @return
	 */
	public boolean isError() {
		return errorModel.size()>0;
	}
	/**
	 * 返回验证错误的model
	 * @return
	 */
	public Map<String, String> getErrorModel() {
		return errorModel;
	}
	public void addErrorModel(String paramName,String errorResult) {
		errorModel.put(paramName, errorResult);
	}	
}
