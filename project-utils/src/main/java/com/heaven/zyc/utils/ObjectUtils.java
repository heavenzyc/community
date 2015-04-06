/**
 * @(#)ObjectUtils.java
 * Copyright 2013 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.utils;

import java.io.*;

/**
 * @author xu.jianguo
 * @date 2013-1-7 description
 */
public class ObjectUtils {
	 
	/**
	 * 对象转换成字节数组,要求传入的对象必须实现序列号接口.
	 * 
	 * @param obj
	 * @return byte[]
	 */
	public static byte[] ObjectToByte(Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bo = null;
		ObjectOutputStream oo = null;
		try {
			bo = new ByteArrayOutputStream();
			oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			bytes = bo.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("object to byte error.",e);
		}
		finally{
			if(bo!=null) {
				try {
					bo.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			if(oo!=null) {
				try {
					oo.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return bytes;
	}

	/**
	 * 字节数组转换成对象
	 * 
	 * @param bytes
	 * @return Object 取得结果后强制转换成你存入的对象类型
	 */
	public static Object ByteToObject(byte[] bytes) {
		Object obj = null;
		ByteArrayInputStream bi = null;
		ObjectInputStream oi = null;
		try {
			bi = new ByteArrayInputStream(bytes);
			oi = new ObjectInputStream(bi);
			obj = oi.readObject();
		} catch (Exception e) {
			throw new RuntimeException("byte to object error.",e);
		}
		finally{
			if(bi!=null) {
				try {
					bi.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			if(oi!=null) {
				try {
					oi.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return obj;
	}
}
