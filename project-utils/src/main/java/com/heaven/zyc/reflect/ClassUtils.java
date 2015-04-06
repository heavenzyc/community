/**
 * @(#)ClassUtil.java
 *
 * Copyright 2010 naryou, Inc. All rights reserved.
 */

package com.heaven.zyc.reflect;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

/**
 * 
 * @author jianguo.xu
 * @version 1.0,Jun 17, 2010
 */
public final class ClassUtils {
	 
	private static final String CGLIB_CLASS_SEPARATOR = "$$";
	public static final String getRealClassName(Class<?> clazz) {
		return getRealClass(clazz).getName();
	}
	/**
	 * 得到非CGLIB代理对象的原始class
	 * @author jianguo.xu
	 * @param clazz
	 * @return
	 */
	public static final Class<?> getRealClass(Class<?> clazz) {
		if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null && !Object.class.equals(superClass)) {
				return superClass;
			}
		}
		return clazz;
	}
	
	public static final Class<?> getRealClass(Object obj) {
		return getRealClass(obj.getClass());
	}
	/**
	 * 比较两个类是否一样<br/>
	 * 考虑了hibernate缓存代理对象的因素
	 * @author jianguo.xu
	 * @param clazz
	 * @param clazzOther
	 * @return
	 */
	public static final boolean equals(Class<?> clazz,Class<?> clazzOther) {
		if(clazz == clazzOther) return true;
		return getRealClassName(clazz).equals(getRealClassName(clazzOther))?true:false;
	}
	
	
	
	/**
	 * 搜索class path下的类的名称集合（包名+类名）
	 * @author jianguo.xu
	 * @param packageName 包名(com.tiger.domain) 如果从根目录查找则输入""
	 * @param recursive 是否递归搜索
	 * @return
	 */
	public static List<String> getClassPathClasseNames(String packageName) {
		String pattern = (new StringBuilder("classpath*:")).append(packageName.replace('.', '/')).append("/**/*.class").toString();
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = null;
		try {
			resources = resourcePatternResolver.getResources(pattern);
		} catch (IOException e) {
			throw new RuntimeException("search classpath packeage error",e);
		}
		MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
		List<String> classNames = new ArrayList<String>();
		for(Resource resource :resources) {
			 MetadataReader reader = null;
			try {
				reader = readerFactory.getMetadataReader(resource);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
             String className = reader.getClassMetadata().getClassName();
            
             classNames.add(className);
		}
		return classNames;
	}
	
	public static Resource[] getResources(String packageName,String suffix) {
		String pattern = (new StringBuilder("classpath*:")).append(packageName.replace('.', '/')).append("/**/*.").append(suffix).toString();
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		try {
			return resourcePatternResolver.getResources(pattern);
		} catch (IOException e) {
			throw new RuntimeException("search classpath packeage error",e);
		}
		
	}
	/**
	 * 搜索class path下的类的class集合（包名+类名）
	 * @author jianguo.xu
	 * @param packageName 包名(com.tiger.domain) 如果从根目录查找则输入""
	 * @param recursive 是否递归搜索
	 * @return
	 */
	public static List<Class<?>> getClassPathClasses(String packageName) {
		List<String> classNames = getClassPathClasseNames(packageName);
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String className : classNames) {
			try {
				classes.add(Class.forName(className));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("class not found : " + className, e);
			}
		}
		return classes;
	}
	/**
	 * 搜索class path下的类的class集合（包名+类名）
	 * @author jianguo.xu
	 * @param packageName 包名(com.tiger.domain) 如果从根目录查找则输入""
	 * @param recursive 是否递归搜索
	 * @param filter class过滤器,当过滤器为null 或accept方法返回true时才添加到集中
	 * @return
	 */
	 
	public static  List<Class<?>> getClassPathClasses(String packageName,ClassFilter filter) {
		List<String> classNames = getClassPathClasseNames(packageName);
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className);
				if(filter == null||filter.accept(clazz)) {
					classes.add(clazz);
				}	
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("class not found : " + className, e);
			}
			catch (Exception e) {
				throw new RuntimeException("class not found : " + className, e);
			}
		}
		return classes;
	}
	
 
	
	/**
	 * 得到指定类下的公开方法和方法的基本命名,<br/>
	 * 如： getType()的基本基本命名为：type
	 * @author jianguo.xu
	 * @param clazz		类
	 * @param includeParrentClass 是否包含父类的get方法<br/>
	 * 只返回java基本类型、基本类型的包装类以及BigDecimal,String 对应的方法
	 * @return 
	 */
	public static Map<Method, String> paraserGet(Class<?> clazz,boolean includeParrentClass) {
		Map<Method, String> map = new HashMap<Method, String>();
		while(!clazz.equals(Object.class)) {
			Map<Method, String> clazzMap = paraserClassGet(clazz);
			map.putAll(clazzMap);
			if(!includeParrentClass) break;
			clazz = clazz.getSuperclass();
		}
		return map;
	}
	
	private static Map<Method, String> paraserClassGet(Class<?> clazz) {
		Map<Method, String> map = new HashMap<Method, String>();
		String[] typeArray = {"String", "Integer", "int",  "Long","long", "Short","short", "Float", "float", 
				"Double", "double","Boolean","boolean","BigDecimal","Date","Calendar"};
		String[] excludeMethods = {"equals","hashCode","toString"};
		Method[] methods = clazz.getDeclaredMethods();
		
		for(Method method : methods) {
			if(!method.getName().startsWith("get")&&!method.getName().startsWith("is"))
				continue;
			if(ArrayUtils.contains(excludeMethods, method.getName()))
				continue;
			if (!ArrayUtils.contains(typeArray, method.getReturnType().getSimpleName()))  
				continue;
			if(method.getGenericParameterTypes().length>0) 
				continue;
			method.setAccessible(true);
			String name = method.getName();
			if(name.startsWith("get"))
				name = name.substring(3,4).toLowerCase()+name.substring(4,name.length());
			if(name.startsWith("is"))
				name = name.substring(2,3).toLowerCase()+name.substring(3,name.length());
			map.put(method, name);
		}
		return map;
	}
	
	/**
	 * 判断一个对象是否是基本类型以及String、BigDecimal、Date
	 * @param object
	 * @return
	 */
	public static boolean isBaseType(Object object) {
		if(object==null) return false;
		String fieldType = object.getClass().getName();
		if (fieldType.equals("java.lang.String")) {
			return true;
		}
		if (fieldType.equals("long") || fieldType.equals("java.lang.Long")) {
			return true;
		}
		if (fieldType.equals("int") || fieldType.equals("java.lang.Integer")) {
			return true;
		}
		if (fieldType.equals("short") || fieldType.equals("java.lang.Short")) {
			return true;
		}
		if (fieldType.equals("float") || fieldType.equals("java.lang.Float")) {
			return true;
		}
		if (fieldType.equals("double") || fieldType.equals("java.lang.Double")) {
			return true;
		}
		if (fieldType.equals("boolean") || fieldType.equals("java.lang.Boolean")) {
			return true;
		}
		if (fieldType.equals("java.math.BigDecimal")) {
			return true;
		}
		if (fieldType.equals("java.sql.Date")) {
			return true;
		}
		if (fieldType.equals("java.util.Date")) {
			return true;
		}
		if (fieldType.equals("java.util.Calendar")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 得到指定类下的 set方法 和方法对应的属性
	 * @author jianguo.xu
	 * @param clazz		类
	 * 只返回java基本类型、基本类型的包装类以及BigDecimal,String,Date 对应的方法
	 * @return 
	 */
	public static Map<Method,Field> paraserSet(Class<?> clazz) {
		Map<Method, Field> map = new HashMap<Method, Field>();
		Method[] methods = clazz.getDeclaredMethods();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Method m = getMethod(field,methods);
			if(m == null)continue;
			map.put(m, field);
		} 
		return map;
	}
	
	private static Method getMethod(Field field,Method[] methods) {
		String[] typeArray = {"String", "Long","long", "Integer", "int", "Short","short", "Float", "float", 
				"Double", "double","Boolean","boolean","BigDecimal","Date","Calendar"};
		String fieldTypeName = field.getType().getSimpleName();
		if (!ArrayUtils.contains(typeArray,fieldTypeName))return null;
		for(Method method : methods) {
			if(method.getParameterTypes().length!=1) continue;
			Class<?> parameterClazz = method.getParameterTypes()[0];
			if (!parameterClazz.getSimpleName().equals(fieldTypeName))  
				continue;
			method.setAccessible(true);
			String methodName = method.getName();
			String fieldName = field.getName();
			String propertieName ="set" + fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);
			if(methodName.equals(propertieName))return method;
		}
		return null;
	}
	
	
	/**
	 * 根据类型返回改类型的对象
	 * 改类型必须有默认的构造方法
	 * @author jianguo.xu
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public static<T> T instanceObject(Class<T> clazz)  {
		return instanceObject(clazz,null);
	}
	/**
	 * 根据类型返回改类型的对象
	 * @author jianguo.xu
	 * @param <T> 类型
	 * @param clazz	构造方法参数
	 * @return
	 */
	public static<T> T instanceObject(Class<T> clazz,Object[] parameters)  {
		try {
			Constructor<T> con = clazz.getDeclaredConstructor(objectConvertClass(parameters));
			con.setAccessible(true);
			return (T) con.newInstance(parameters);
		} catch (Exception e) {
			throw new RuntimeException("init Object error : "+clazz.getName(),e);
		}
	}
	
	private static Class<?>[] objectConvertClass(Object... parameters) {
		if(parameters == null||parameters.length == 0) return null;
		Class<?>[] types = new Class<?>[parameters.length];
		for(int i = 0;i<parameters.length;i++) {
			Object parameter = parameters[i];
			types[i] = parameter.getClass();
		}
		return types;
	}
	/**
	 * class过滤器
	 * @author tiger
	 *
	 */
	public static interface ClassFilter {
	    boolean accept(Class<?> clazz);
	}
 
}
