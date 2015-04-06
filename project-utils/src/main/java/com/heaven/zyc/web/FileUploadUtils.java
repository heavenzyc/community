/**
 * @(#)FileUtils.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.heaven.zyc.utils.DateUtils;
import com.heaven.zyc.utils.StringUtils;
import com.heaven.zyc.utils.ConfigUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * description
 *
 * @author jianguo.xu
 * @version 1.0,2011-3-17
 */
public class FileUploadUtils {
	private static final Log LOG = LogFactory.getLog(FileUploadUtils.class);
	private static final String DEFALUT_SUFFIX = "temp";
	private static final String FILE_SEPARATOR = "/";
	/**
	 * 上传文件的最大文件字节数(单位k)
	 */
	private static final long DEFAULT_MAX_SIZE = 2048;
	/**
	 * 上传文件,为保证文件的上传成功，用时间戳作为文件名
	 *
	 * @author jianguo.xu
	 * @param request
	 *            http request 请求
	 * @param fileParamter
	 *            上传的文件请求参数
	 * @return 返回相对于web工程的绝对路径+文件名
	 * @throws java.io.IOException
	 */
	public static String uploadImage(HttpServletRequest request, String fileParamter,boolean absolute) throws IOException {
		return uploadImage(request, fileParamter,absolute, DEFAULT_MAX_SIZE, null);
	}
	/**
	 * 上传文件,为保证文件的上传成功，用时间戳作为文件名<br/>
	 * 如果文件名不存在，则返回null
	 *
	 * @author jianguo.xu
	 * @param request
	 *            http request 请求
	 * @param fileParamter
	 *            上传的文件请求参数
	 * @param 上传文件允许的最大字节数
	 *            (单位k)
	 * @param 上传文件允许的后缀名
	 * @return 返回相对于web工程的绝对路径+文件名
	 * @throws java.io.IOException
	 */
	public static String uploadImage(HttpServletRequest request, String fileParamter, boolean absolute,long maxSize,
			String[] includesuffixs) throws IOException {
		return uploadImage(request, fileParamter, ConfigUtils.UPLOAD_IMAGE_PATH,absolute, maxSize, includesuffixs);
	}

	public static String uploadImage(HttpServletRequest request, String fileParamter, String uploadPath, boolean absolute,
			long maxSize,
			String[] includesuffixs) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile(fileParamter);
		String filename = multipartFile.getOriginalFilename();
		if (StringUtils.isNullOrEmpty(filename)) {
			LOG.warn("filename is empty, fileParamter=" + fileParamter + ",uploadPath=" + uploadPath + ",absolute=" + absolute + ",includesuffixs=" + Arrays.toString(includesuffixs));
			return null;
		}
		String[] split = filename.split("\\.");
		String suffix = split.length == 2 ? split[1] : DEFALUT_SUFFIX;
		return uploadImage(request, fileParamter, uploadPath, absolute,suffix, maxSize, includesuffixs);
	}



	private static String getUploadPath(String uploadPath,boolean absolute) {

		String path = uploadPath.startsWith(FILE_SEPARATOR) ? uploadPath : "/"+uploadPath;
		if(!absolute) {
			String webRoot = ConfigUtils.WEB_ROOT.endsWith(FILE_SEPARATOR) ?
					ConfigUtils.WEB_ROOT.substring(1) : ConfigUtils.WEB_ROOT;
			path = webRoot+ path;
		}
		String today = DateUtils.format(new Date(), "yyyyMMdd");
		path = path.endsWith(FILE_SEPARATOR) ? path + today : path + FILE_SEPARATOR + today + "/";
		initDir(path);
		return path;
	}

	private static void initDir(String uploadPath) {
		File directory = new File(uploadPath);
		if (!directory.isDirectory()) {
			directory.mkdirs();
		}
	}

	/**
	 * 上传文件,为保证文件的上传成功，用时间戳作为文件名
	 *
	 * @author jianguo.xu
	 * @param request
	 *            http request 请求
	 * @param fileParamter
	 *            上传的文件请求参数
	 * @param uploadPath 上传路径
	 * @param suffix
	 *            文件后缀名
	 * @param includesuffixs 可包括的后缀名数组
	 * @param absolute 是否是绝对路径
	 * true表示uploadPath表示为绝对路径，false表示uploadPath表示为相对于web工程的相对路径
	 * @return 返回上传的路径+文件名,如果absolute=true则返回的路径为绝对路径，否则为相对于web工程的路径
	 * @throws java.io.IOException
	 */
	public static String uploadImage(HttpServletRequest request, String fileParamter, String uploadPath,boolean absolute,String suffix,
			long maxSize, String[] includesuffixs) throws IOException {
		uploadPath = getUploadPath(uploadPath,absolute);
		String filePrefix = new Long(System.currentTimeMillis()).toString();
		String fileName = uploadPath.endsWith(FILE_SEPARATOR) ? uploadPath + filePrefix : uploadPath + FILE_SEPARATOR
				+ filePrefix;
		fileName = fileName + "." + suffix;
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile(fileParamter);
		assertCanUpload(multipartFile, suffix, maxSize, includesuffixs);
		File destFile = new File(fileName.toString());
		try {
			multipartFile.transferTo(destFile);
		} catch (Exception e) {
			LOG.error("upload image error", e);
			return null;
		}
		if(absolute) {
			return fileName;
		}
		else {
			String returnFileName = fileName.substring(ConfigUtils.WEB_ROOT.length());
			return returnFileName.startsWith(FILE_SEPARATOR) ? returnFileName : "/" + returnFileName;
		}

	}

	private static void assertCanUpload(MultipartFile multipartFile, String suffix, long maxSize,
			String[] includesuffixs) throws IOException {
		if (multipartFile == null || multipartFile.getSize() == 0) {
			throw new IOException("上传文件不存在");
		}
		if (multipartFile.getSize() > maxSize * 1024) {
			throw new IOException("上传文件不能超过  " + maxSize + " K");
		}
		if (includesuffixs == null || includesuffixs.length == 0)
			return;
		boolean eixstSuffix = false;
		for (String includesuffix : includesuffixs) {
			if (includesuffix.toLowerCase().equals(suffix.toLowerCase())) {
				eixstSuffix = true;
				break;
			}
		}
		if (!eixstSuffix) {
			throw new IOException("上传的文件格式不正确");
		}
	}

	/**
	 * 获得输入流
	 *
	 * @param request
	 * @param fileParamter
	 * @return
	 * @throws java.io.IOException
	 */
	public static InputStream getInputStream(HttpServletRequest request, String fileParamter) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile(fileParamter);
		if (multipartFile != null)
			return multipartFile.getInputStream();
		return null;
	}

	public static MultipartFile getFile(HttpServletRequest request, String fileParamter) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile(fileParamter);

		return multipartFile;
	}

}
