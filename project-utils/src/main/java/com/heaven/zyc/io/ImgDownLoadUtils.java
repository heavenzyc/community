/**
 * @(#)ImgDownLoadUtils.java
 * Copyright 2012 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jianguo.xu
 * @date 2012-11-11
 * description
 */
public class ImgDownLoadUtils {
	private static final Log LOG = LogFactory.getLog(ImgDownLoadUtils.class);
	/**
	 * 图片下载
	 * @author jianguo.xu
	 * @param sourceUrl
	 * @param destPath
	 * @param fileName
	 * @return
	 */
	public static String downLoad(String sourceUrl, String destPath,
			String fileName) {
		if (!destPath.endsWith(File.separator))
			destPath += File.separator;
		File path = new File(destPath);
		if (!path.exists()) {
			path.mkdirs();
		}
		File file = new File(path, fileName);
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		HttpURLConnection httpUrl = null;
		URL url = null;
		int BUFFER_SIZE = 1024;
		byte[] buf = new byte[BUFFER_SIZE];
		int size = 0;
		try {
			url = new URL(sourceUrl);
			httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			fos = new FileOutputStream(file);
			while ((size = bis.read(buf)) != -1) {
				fos.write(buf, 0, size);
			}
			fos.flush();
		} catch (Exception e) {
			LOG.error("down load img error", e);
			return null;
		} finally {
			try {
				fos.close();
				bis.close();
				httpUrl.disconnect();
			} catch (Exception e) {
				LOG.error(e);
			}
		}
		return file.getAbsolutePath().replace("\\", "/");
	}
}
