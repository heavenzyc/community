/**
 * @(#)ObjectConvertInterceptor.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.spring.vaildator;

import com.heaven.zyc.support.spring.vaildator.exception.JsonValidatorException;
import com.heaven.zyc.support.spring.vaildator.exception.ValidatorException;
import com.heaven.zyc.support.spring.vaildator.exception.XmlValidatorException;
import com.heaven.zyc.utils.StringUtils;
import com.heaven.zyc.web.HttpParameterParser;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * 对基于ParamValidator注解的参数验证核心类
 * @author  jianguo.xu
 * @version 1.0,2011-4-27
 */
public class ParamValidatorResolver implements WebArgumentResolver{
	@Override
	public Object resolveArgument(MethodParameter methodParameter,
			NativeWebRequest webRequest) throws Exception {
		Method method = methodParameter.getMethod();
		ParamValidators paramVaildators = method.getAnnotation(ParamValidators.class);
		if(!executeValidator(paramVaildators, methodParameter.getParameterType()))
			return UNRESOLVED;
		try {
			validator(webRequest,paramVaildators.value());
		}	
		catch(ValidatorException ex) {
			if(paramVaildators.resultType() == ResultType.JSON) {
				throw new JsonValidatorException(ex.getMessage());
			}
			if(paramVaildators.resultType() == ResultType.XML) {
				throw new XmlValidatorException(ex.getMessage());
			}
			else {
				ValidatorResult result = new ValidatorResult();
				result.addErrorModel(ex.getName(), ex.getMessage());
				return result;
			}
		}
		return UNRESOLVED;
	}
	/**
	 * 判断是否执行参数校验
	 * @param paramVaildators
	 * @param parameterType
	 * @return
	 * @author  xu.jianguo
	 */
	private boolean executeValidator(ParamValidators paramVaildators,Class<?> parameterType) {
		if(paramVaildators == null) return false;
		
		ResultType type = paramVaildators.resultType();
		if(type ==ResultType.JSON||type ==ResultType.XML)
			return true;
		 
		if(type ==ResultType.MODE&&parameterType.equals(ValidatorResult.class))
			return true;
		return false;
	}
	private boolean isJsonErrorResult(Method method) {
		Class<?>[] classes = method.getParameterTypes();
		for(Class<?>  clazz : classes) {
			if(clazz.equals(ValidatorResult.class))
				return false;
		}
		return true;
	}
	private void validator(NativeWebRequest webRequest,ParamValidator[] paramVaildators){
		for(ParamValidator paramVaildator : paramVaildators) {
			HttpParameterParser HttpParser = HttpParameterParser.newInstance((HttpServletRequest)webRequest.getNativeRequest());
			String values[] = HttpParser.getStringArray(paramVaildator.param());
			String paramName = paramVaildator.paramName();
			if(paramVaildator.required()&&(values==null||values.length ==0)) {
				throw new ValidatorException(paramVaildator.param(),paramName+" 不能为空");
			}
			for(String value : values) {
				if(StringUtils.isNullOrEmpty(value)) continue;
				for(int i = 0;i<paramVaildator.vaildatorTypes().length;i++) {
					RegexValidatorType vType = paramVaildator.vaildatorTypes()[i];
					if(!vType.validator(value)) {
						throw new ValidatorException(paramVaildator.param(),vType.getErrorMsg(paramName));
					}
				}
				validatorLength(value,paramVaildator.param(),paramName,paramVaildator.length());
				validatorMin(value, paramVaildator.param(),paramName, paramVaildator.min());
				validatorMax(value, paramVaildator.param(),paramName, paramVaildator.max());
				validatorDecimalMin(value, paramVaildator.param(),paramName, paramVaildator.DecimalMin());
				validatorDecimalMax(value, paramVaildator.param(),paramName, paramVaildator.DecimalMax());
				validatorCustomRegex(value, paramVaildator.param(), paramName, paramVaildator.customRegex(), paramVaildator.customErrorMsg());
			}
		}
	}
	/**
	 * 对长度进行验证
	 * @author jianguo.xu
	 * @param value
	 * @param paramName
	 * @param dataLength
	 */
	private void validatorLength(String value,String name,String paramName,int[] dataLength) {
		if(dataLength.length == 0) return;
		if(dataLength.length == 1)  {
			if(value.length()!=dataLength[0])
				throw new ValidatorException(name,paramName+" 的长度必须为 "+dataLength[0]);
		}
		else {
			if(value.length()<dataLength[0]||value.length()>dataLength[1]) {
				throw new ValidatorException(name,paramName+" 的长度必须在  "+dataLength[0]+" 和 "+dataLength[1]+" 之间");
			}
		}
	}
	/**
	 * 验证最小值
	 * @param value
	 * @param paramName
	 * @param min
	 */
	private void validatorMin(String value,String name,String paramName,long min) {
		long longValue = Long.MIN_VALUE;
		if(longValue == min) return;
		try {
			longValue = Long.parseLong(value);
		}
		catch(NumberFormatException e) {
			throw new ValidatorException(name,paramName+" 必须为数字");
		}
		if(longValue<min) {
			throw new ValidatorException(name,paramName+" 必须大于等于 "+min);
		}
	}
	/**
	 * 验证最大值
	 * @param value
	 * @param paramName
	 * @param max
	 */
	private void validatorMax(String value,String name,String paramName,long max) {
		long longValue = Long.MAX_VALUE;
		if(longValue == max) return;
		try {
			longValue = Long.parseLong(value);
		}
		catch(NumberFormatException e) {
			throw new ValidatorException(name,paramName+" 必须为数字");
		}
		if(longValue>max) {
			throw new ValidatorException(name,paramName+" 必须小于等于 "+max);
		}
	}
	
	/**
	 * 验证最Decimal小值
	 * @param value
	 * @param paramName
	 */
	private void validatorDecimalMin(String value,String name,String paramName,String decimalMinStr) {
		if(StringUtils.isNullOrEmpty(decimalMinStr))return;
		BigDecimal decimalMin = new BigDecimal(decimalMinStr);
		BigDecimal decmailValue = null;
		try {
			decmailValue = new BigDecimal(value);
		}
		catch(NumberFormatException ex) {
			throw new ValidatorException(name,paramName+" 必须为数字");
		}
		if(decmailValue.compareTo(decimalMin)<0) {
			throw new ValidatorException(name,paramName+" 必须大于等于 "+decimalMinStr);
		}		
	}
	/**
	 * 验证最大值
	 * @param value
	 * @param paramName
	 */
	private void validatorDecimalMax(String value,String name,String paramName,String decimalMaxStr) {
		if(StringUtils.isNullOrEmpty(decimalMaxStr))return;
		BigDecimal decimalMax = new BigDecimal(decimalMaxStr);
		BigDecimal decmailValue = null;
		try {
			decmailValue = new BigDecimal(value);
		}
		catch(NumberFormatException ex) {
			throw new ValidatorException(name,paramName+" 必须为数字");
		}
		if(decmailValue.compareTo(decimalMax)>0) {
			throw new ValidatorException(name,paramName+" 必须小于等于 "+decimalMaxStr);
		}
	}
	
	/**
	 * 验证最大值
	 * @param value
	 * @param paramName
	 */
	private void validatorCustomRegex(String value,String name,String paramName,String customRegex,String customErrorMsg) {
		if(StringUtils.isNullOrEmpty(customRegex))return;
		if(Pattern.matches(customRegex, value)) {
			return;
		}
		throw new ValidatorException(name,customErrorMsg);
		
		 
	}
}