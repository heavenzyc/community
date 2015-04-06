/**
 * @(#)ShortUlrUtils.java
 * Copyright 2013 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.web;

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author xu.jianguo
 * @date 2013-4-24 
 * 短URL地址工具类
 */
public final class ShortUlrUtils {

	private static final String[] chars = new String[] { "a", "b", "c", "d",
			"e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
			"r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3",
			"4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z"};
	private static final Random RANDOM = new Random();
	/**
	 * 根据URL地址输出short url数组
	 * @param url			long url地址
	 * @param shorUrlLen 	short url长度
	 * @param arrayLen	数组长度,数组长度只能是4-8之间
	 * @return
	 * @author  xu.jianguo
	 */
	public static String[] shortUrl(String url, int shorUrlLen, int arrayLen) {
		String hex = DigestUtils.shaHex(url);
	 
		//String hex = DigestUtils.md5Hex(url);
		return shortUrls(hex, shorUrlLen, arrayLen);
	}
	/**
	 *  根据URL地址输出short url<br/>
	 *  
	 * @param url 			long url地址
	 * @param shorUrlLen	short url长度
	 * @return
	 * @author  xu.jianguo
	 */
	public static String shortUrl(String url, int shorUrlLen) {
		 
		return shortUrl(url, shorUrlLen, 4)[RANDOM.nextInt(4)];
	}
	private static String[] shortUrls(String hex, int shorUrlLen, int arrayLen) {
		if (shorUrlLen < 5 || shorUrlLen > 8) {
			throw new RuntimeException("shorUrl len 必须介于5和8之间");
		}
		if (arrayLen == 0) {
			throw new RuntimeException("arrayLen 必须大于0");
		}
		hex = hex.replaceAll("[^a-z0-9A-Z]", "");
		String[] resUrls = new String[arrayLen];
		int preKeyLen = (hex.length() / arrayLen);
		if (preKeyLen < 5) {
			throw new RuntimeException("输出数组过大,每组解析长度不能小于5");
		}
		for (int i = 0; i < arrayLen; i++) {
			int quantityEndIndex = (i + 1) * preKeyLen;
			if (i == arrayLen - 1) {
				quantityEndIndex = hex.length();
			}
			String subString = hex.substring(i * preKeyLen, quantityEndIndex);
			if (subString.length() > 12) {
				throw new RuntimeException("加密字符串长度过长，请适当增加输出数组长度或减少加密字符串长度");
			}
			
			//long lHexLong = 0x3FFFFFFF & Long.parseLong(subString, Character.MAX_RADIX);
			
			long lHexLong =  Long.parseLong(subString, Character.MAX_RADIX);
			int lHexLongLen = Long.toBinaryString(lHexLong).length();
			String outStr = "";
			for (int j = 0; j < shorUrlLen; j++) {
				// 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
				long index = (chars.length-1) & lHexLong;
				outStr += chars[(int) index];
				// 每次循环按位右移 n 位
				lHexLong = lHexLong >> lHexLongLen/shorUrlLen;
			}
			resUrls[i] = outStr;
		}
		return resUrls;

	}
	
	 
}
