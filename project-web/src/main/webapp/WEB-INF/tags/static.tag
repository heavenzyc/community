<%@tag import="java.lang.reflect.Field"%>
<%@tag import="java.lang.reflect.Method"%>
<%@tag import="com.heaven.zyc.utils.StringUtils"%>
<%@ tag pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 
<%@attribute name="value" 
	required="true" 
	rtexprvalue="false" 
	description="类名+@+静态属性(静态方法) 支持方法传递参数，但目前只支持基本类型和包装类型、String、BigDecimal等"
%>
<%@attribute name="isMethod" required="false" rtexprvalue="false" type="java.lang.Boolean" description="是否是method,默认是false"%>
<%@attribute name="var" required="false" rtexprvalue="false" description="如果定义了var将把值放在var变量中，如果没有定义则直接输出到jsp中"%>
<%
	if (StringUtils.isNullOrEmpty(value))
		throw new RuntimeException("value is null");
	String[] splits = value.split("@");
	if (splits.length != 2) {
		throw new RuntimeException("static type define error,not exist @");
	}
	String objectName = splits[0];
	String filedOrMethodName = splits[1];
	Class<?> clazz = null;
	try {
		clazz = Class.forName(objectName);
	} catch (ClassNotFoundException e) {
		throw new RuntimeException("class not found : " + objectName, e);
	}
	
	if(isMethod !=null && isMethod) {
		try {
			Method method = clazz.getMethod(filedOrMethodName);
			Object object = method.invoke(null);
			if(var!=null) {
				jspContext.setAttribute(var, object, 2);
			}
			else {
				out.print(object);
			}	 
		} catch (Exception e) {
			throw new RuntimeException("method not found : " + filedOrMethodName, e);
		}
	}
	else {
		try {
			Field field = clazz.getField(filedOrMethodName);
			Object object = field.get(null);
			if(var!=null) {
				jspContext.setAttribute(var, object, 2);
			}
			else {
				out.print(object);
			}
		} catch (Exception e) {
			throw new RuntimeException("field not found : " + filedOrMethodName, e);
		}
	}
%>
