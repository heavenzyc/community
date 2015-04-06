/**
 * @(#)EnumType.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;
import org.hibernate.util.ReflectHelper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * hibernate 自定义枚举客户化映射类型<br/>
 * 参考配置：
 * 
 * @TypeDefs({@TypeDef(name = "status" , typeClass = EnumType. class ,
                           parameters = {
   @Parameter(name = "class" , value =
                   "demo.domain.one2one.testtwo.Order$StatusEnum" ),
   @Parameter(name = "field" , value = "status" ),
   @Parameter(name = "method" , value = "getStatusEnum" )}) })
  @Type(type = "status" ) 
 * Parameter： 
 * class:枚举类名(必填) 
 * field:需要保存进数据库的字段名(选填  默认为"value")，该字段默认为int类型 
 * method:根据字段值获取枚举的静态方法名(选填 ,默认为"get")
 * valueType:表示数据库字段的类型默认为int类型，也可以为string
 * @author jianguo.xu
 * @version 1.0,2011-1-5
 */
public class EnumType<T> implements UserType, ParameterizedType {
		
	private Class<? extends Enum<?>> enumClass;
	private Field field;
	private Method method;
	private ValueType valueType;

	public static final String CLASSNAME_KEY = "class";
	public static final String FIELD_KEY = "field";
	public static final String METHOD_KEY = "method";
	public static final String VALUE_TYPE_KEY = "VALUE_TYPE";
	
	public static final String DEFALUT_FIELD_VALUE = "value";
	public static final String DEFALUT_METHOD_VALUE = "get";
	public static final String INT_VALUE_TYPE = "INT";
	public static final String STRING_VALUE_TYPE = "STRING";
	
	private String className;
	private String fieldName;
	private String methodName;
	private String valueTypeName;

	@SuppressWarnings("unchecked")
	public void setParameterValues(Properties properties) {
		intitValue(properties);
		valueType = ValueType.get(valueTypeName);
		if (className == null || className.trim().equals(""))
			throw new HibernateException("Enum class not found");
		try {
			enumClass = ReflectHelper.classForName(className, this.getClass())
					.asSubclass(Enum.class);
		} catch (ClassNotFoundException e) {
			throw new HibernateException("Enum class not found : "+className, e);
		}
		try {
			field = enumClass.getDeclaredField(fieldName);
		} catch (Exception e) {
			throw new HibernateException("Enum field not found : "+className, e);
		}
		String fieldType = field.getType().getSimpleName();
		if (!fieldType.equalsIgnoreCase(valueType.name())) {
			throw new HibernateException("Enum field :"+fieldName+" is must "+valueType.name());
		}
		field.setAccessible(true);
		try {
			method = enumClass.getDeclaredMethod(methodName, field.getType());
		} catch (Exception e) {
			throw new HibernateException("Enum method not found : "+methodName+" in class "+className, e);
		}
		method.setAccessible(true);
		
	}

	private void intitValue(Properties properties) {
		className = properties.getProperty(CLASSNAME_KEY);
		fieldName = properties.getProperty(FIELD_KEY, DEFALUT_FIELD_VALUE);
		methodName = properties.getProperty(METHOD_KEY, DEFALUT_METHOD_VALUE);
		valueTypeName = properties.getProperty(VALUE_TYPE_KEY, INT_VALUE_TYPE);
	}

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}
	/**
	 * 自定义数据类型的比对方法
	 * 此方法将用作脏数据检查，参数x、y分别为数据的两个副本
	 * 如果equals方法返回false,则Hibernate将认为数据发生变化,并将变化更新到数据库表中
	 */
	@SuppressWarnings("rawtypes")
	public boolean equals(Object x, Object y) throws HibernateException {
		if(!(x instanceof Enum))
			return false;
		if(!(y instanceof Enum))
			return false;
		Enum xe = (Enum) x;
		Enum ye = (Enum) y;
		return xe == ye;
	}

	public int hashCode(Object x) throws HibernateException {
		return x == null ? 0 : x.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		/*if (rs.wasNull())
			return null;
		String resultStr = rs.getString(names[0]);*/
		@SuppressWarnings("deprecation")
		String resultStr = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
		if (resultStr == null || resultStr.trim().equals(""))
			return null;
		try {
			if(valueType == ValueType.INT)
				return method.invoke(null, Integer.parseInt(resultStr.trim()));
			return method.invoke(null, resultStr.trim());
		} catch (Exception e) {
			throw new HibernateException("invoke enum static method error.", e);
		}
	}

	public void nullSafeSet(PreparedStatement ps, Object value, int index)
			throws HibernateException, SQLException {
		if (value == null) {
			if(valueType == ValueType.INT) {
				ps.setNull(index, Types.INTEGER);
			}
			else {
				ps.setNull(index, Types.VARCHAR);
			}
		} else {
			try {
				if(valueType == ValueType.INT) {
					ps.setInt(index, field.getInt(value));
				}
				else {
					ps.setString(index, value.toString());
				}
			} catch (Exception e) {
				throw new HibernateException("get enum field value error.", e);
			}
		}
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	public Class<? extends Enum<?>> returnedClass() {
		return enumClass;
	}

	public int[] sqlTypes() {
		return new int[] { Types.INTEGER };
	}
	
	private enum ValueType{
		INT,STRING;
		static ValueType get(String name) {
			if(name.equalsIgnoreCase((STRING.name())))
				return STRING;
			return INT;
		}
	}
}
