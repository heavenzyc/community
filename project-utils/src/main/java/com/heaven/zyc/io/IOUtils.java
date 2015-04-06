/**
 * @(#)IOUtils.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * description
 * @author  jianguo.xu
 * @version 1.0,2011-10-11
 */
public final class IOUtils {
	private static final Log LOG = LogFactory.getLog(IOUtils.class);
	public static final String GBK="GBK";
	public static final String UTF8="UTF-8";
	/**
	 * 把输入流转换成字节数组
	 * @author jianguo.xu
	 * @param ins
	 * @return
	 */
	public static byte[] inputStreamToBytes(InputStream ins) {
		try {
			int len = ins.available();
			byte[] bs = new byte[len];
			ins.read(bs);
			return bs;
		} catch (IOException e) {
			LOG.error("inputStream to bytes error.",e);
			return new byte[0];
		}
	}
	
	
	/**
	 * 把输入流转内容换成字符串<br/>
	 * 如果转换失败则返还null<br/>
	 * 默认采用GBK的方式转换
	 * @author jianguo.xu
	 * @param ins
	 * @return
	 */
	public static String inputStreamToString(InputStream ins) {
		return inputStreamToString(ins, GBK);
	}
	
	public static String inputStreamToString(InputStream ins,String charsetName) {
		Reader reader = null;
		try {
			reader = new InputStreamReader(ins,charsetName);
		} catch (UnsupportedEncodingException e) {
			LOG.error("InputStream to String error.",e);
			return null;
		}
		StringBuilder sb = new StringBuilder();
		char[] bs = new char[1024];
		try {
			for(int len = 0;(len=reader.read(bs, 0, bs.length))!=-1;) {
				sb.append(bs, 0, len);
			}
		} catch (IOException e) {
			LOG.error("InputStream to String error.",e);
			return null;
		}
		finally{
			try {
				ins.close();
			} catch (IOException e) {
				LOG.error("close inputStream error.",e);
			}
		}		
		return sb.toString();
	}
	
	 
	
	
	public static String fileToString(File file) {
		 return fileToString(file, GBK);
	}
	
	public static String fileToString(File file,String charsetName) {
		InputStream ins = null;
		try {
			ins = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			LOG.error("file to String error.",e);
			return null;
		}
		return inputStreamToString(ins,charsetName);
	}
	
	/**
	 * 创建文件，如果目录不存在，则先创建目录
	 * 文件内容为GBK格式
	 * @author jianguo.xu
	 * @param filePath
	 * @param fileName
	 * @param content
	 * @throws java.io.IOException
	 */
	public static String createFile(String filePath,String fileName,String content) throws IOException {
		return createFile(filePath, fileName, content, GBK);
	}
	
	public static String createFile(String filePath,String fileName,String content,String charSet) throws IOException {
		filePath = filePath.endsWith(File.separator)?filePath:(filePath+File.separator);
		File dir = new File(filePath);
		if(!dir.exists())dir.mkdirs();
		String outPutFile = filePath+fileName;
		FileOutputStream fos = new FileOutputStream(outPutFile);
		fos.write(content.getBytes(charSet));
		return outPutFile;
	}
 
	
}
