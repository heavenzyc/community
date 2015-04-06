/**
 * @(#)FileUtils.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.web;

import com.heaven.zyc.io.ImageUtils;
import com.heaven.zyc.utils.DateUtils;
import com.heaven.zyc.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * description
 * 
 * @author jianguo.xu
 * @version 1.0,2011-3-17
 */
public class ImageUploadUtils {
	
	private static final String FILE_SEPARATOR = "/";
	public static PicMetadata uploadImage(UploadParam uploadParam) throws IOException {
		try {
			assertCanUpload(uploadParam);
			String path = calUploadPath(uploadParam);
			String picName = calFileName(uploadParam);
			String fileName = path+picName;
			writeToDestFile(uploadParam, fileName);
			return parserMetadata(uploadParam, fileName);
			 
		} 
		catch(Exception e){
			throw new IOException("上传文件失败",e);
		}
	}
	
	private static PicMetadata  parserMetadata(UploadParam uploadParam,String fileName) throws IOException {
		PicMetadata picMetadata = new PicMetadata();
		picMetadata.setAbsPath(fileName);
		String relativePath = FILE_SEPARATOR+fileName.substring(getWebRoot(uploadParam.getPicDomainWebRootPath()).length());
		relativePath = relativePath.replaceAll(FILE_SEPARATOR+"+", FILE_SEPARATOR);
		picMetadata.setRelativePath(relativePath);
		String picDomain = uploadParam.getPicDomain();
		HttpServletRequest request = uploadParam.getRequest();
		picDomain  =  StringUtils.isNullOrEmpty(picDomain)
				?request.getServerName()+":"+request.getServerPort()+request.getContextPath()
				:picDomain;
		picMetadata.setUrl(picDomain+relativePath);
		MultipartFile multipartFile = getFile(uploadParam.getRequest(), uploadParam.getFileParamter());	 
		BufferedImage image = ImageIO.read(new File(fileName));
		picMetadata.setWidth(image.getWidth());
		picMetadata.setHeight(image.getHeight());
		picMetadata.setImageType(getImageType(multipartFile));
		return picMetadata;
	}
	/**
	 * 把文件写入到文件中<br/>
	 * 包括对文件的缩放或剪切处理<br/>
	 * @param uploadParam
	 * @param fileName
	 * @throws java.io.IOException
	 * @author  xu.jianguo
	 */
	private static void writeToDestFile(UploadParam uploadParam,String fileName) throws IOException{
		MultipartFile multipartFile = getFile(uploadParam.getRequest(), uploadParam.getFileParamter());
		ZoomParam zoomParam = uploadParam.getZoomParam();
		CutParam cutParam = uploadParam.getCutParam();
		if(zoomParam==null&&cutParam==null) {
			multipartFile.transferTo(new File(fileName));
			return;
		}
		InputStream ins = null;
		try {
			ins = multipartFile.getInputStream();
			if(zoomParam!=null) {
				if(zoomParam.isRate()) {
					ImageUtils.zoom(ins, fileName, zoomParam.getWidth(), zoomParam.getHeight(), true, getImageType(multipartFile));
				}
				else {
					ImageUtils.zoomAndFillBack(ins, fileName, zoomParam.getWidth(), 
							zoomParam.getHeight(), zoomParam.getBlackGround(), ImageUtils.Position.CENTER, getImageType(multipartFile));
				}
			}
			else {

                ImageUtils.ImageType imageType = ImageUtils.ImageType.getByContentSubtype(uploadParam.getRequest().getContentType());
                if(imageType == null) imageType = ImageUtils.ImageType.JPG;
				ImageUtils.cut(ins, fileName, cutParam.getLeftTopPoint(), cutParam.getRightBottomPoint(),imageType);
			}
		} catch (Exception e) {
			throw new IOException("上传文件失败",e);
		}
		finally {
			if(ins != null) {
				ins.close();
			}
		}
	}
	/**
	 *  验证上传参数是否正确
	 * @param uploadParam
	 * @throws java.io.IOException
	 * @author  xu.jianguo
	 */
	private static void assertCanUpload(UploadParam uploadParam) throws IOException {
		MultipartFile multipartFile = getFile(uploadParam.getRequest(), uploadParam.getFileParamter());
		if (multipartFile == null || multipartFile.getSize() == 0) {
			throw new IOException("上传文件不存在");
		}
		
		if (multipartFile.getSize() > uploadParam.getImageMaxSize() * 1024) {
			throw new IOException("上传文件不能超过  " + uploadParam.getImageMaxSize() + " K");
		}
		
		String contentType = multipartFile.getContentType();
		String typeName = contentType.split("/")[0];
		String subType = contentType.split("/")[1].trim().toLowerCase();
		if(!typeName.toLowerCase().equals("image")|| ImageUtils.ImageType.getByContentSubtype(subType)==null) {
			throw new IOException("上传的文件格式不支持");
		}
		ImageUtils.ImageType[] includeImageTypes = uploadParam.getIncludeImageTypes();
		if (includeImageTypes == null || includeImageTypes.length == 0)
			return;
		for (ImageUtils.ImageType includeImageType : includeImageTypes) {
			if(includeImageType.getHttpContentSubtype().equals(subType)) {
				return;
			}
		}
		throw new IOException("上传的文件格式不支持");
	}
	/**
	 * 计算图片文件名字
	 * @param uploadParam
	 * @return
	 * @author  xu.jianguo
	 */
	private static String calFileName(UploadParam uploadParam) {
		String filePrefix = new Long(System.currentTimeMillis()).toString();
		MultipartFile multipartFile = getFile(uploadParam.getRequest(), uploadParam.getFileParamter());
		ImageUtils.ImageType imageType = getImageType(multipartFile);
		String suffix = imageType.name().toLowerCase();
		return filePrefix+"."+suffix;
	}
	/**
	 * 得到图片类型
	 * @param multipartFile
	 * @return
	 * @author  xu.jianguo
	 */
	private static ImageUtils.ImageType getImageType(MultipartFile multipartFile) {
		String contentType = multipartFile.getContentType();
		String type = contentType.split("/")[1];
		return ImageUtils.ImageType.getByContentSubtype(type);
	}
	/**
	 * 计算上传路径
	 * @param uploadParam
	 * @return
	 * @author  xu.jianguo
	 */
	private static String calUploadPath(UploadParam uploadParam) {
		String webRoot =  getWebRoot( uploadParam.getPicDomainWebRootPath());
		StringBuilder sb = new StringBuilder(webRoot+FILE_SEPARATOR);
		if(!StringUtils.isNullOrEmpty(uploadParam.getPathPrefix())) {
			sb.append(uploadParam.getPathPrefix()+FILE_SEPARATOR);
		}
		String today = DateUtils.format(new Date(), "yyyyMMdd");
		sb.append(today+FILE_SEPARATOR);
		String path = sb.toString().replaceAll(FILE_SEPARATOR+"+", FILE_SEPARATOR);
		initDir(path);
		return path;
	}
	/**
	 * 得到web的跟路径
	 * @param picDomainWebRootPath
	 * @return
	 * @author  xu.jianguo
	 */
	private static final String getWebRoot(String picDomainWebRootPath) {
		if(!StringUtils.isNullOrEmpty(picDomainWebRootPath)) {
			return picDomainWebRootPath;
		}		 
		URL url = ImageUploadUtils.class.getResource("/");
		String path = url.getPath();
		if (path.endsWith("/WEB-INF/classes/")) {
			int beginIndex = path.length() - "WEB-INF/classes/".length();
			return path.substring(0, beginIndex);
		}
		return path;
	}
	private static void initDir(String uploadPath) {
		File directory = new File(uploadPath);
		if (!directory.isDirectory()) {
			directory.mkdirs();
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
		MultipartFile multipartFile = getFile(request, fileParamter);
		if (multipartFile != null)
			return multipartFile.getInputStream();
		return null;
	}

	public static MultipartFile getFile(HttpServletRequest request, String fileParamter) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		return multipartRequest.getFile(fileParamter);
		 
	}
	
	public static class UploadParam{
		/**
		 * 上传文件的最大文件字节数(单位k)
		 */
		private static final long DEFAULT_IMAGE_MAX_SIZE = 1024*10;
		 
		/**
		 * request请求
		 */
		private final HttpServletRequest request;
		/**
		 * 上传文件参数名称
		 */
		private final String fileParamter;
		/**
		 * 上传文件的web路径前缀
		 */
		private  String pathPrefix;
		/**
		 * 允许上传的文件类型后缀（不区分大小写）
		 */
		ImageUtils.ImageType[] includeImageTypes;
		/**
		 * 上传图片的最大值
		 */
		private long imageMaxSize = DEFAULT_IMAGE_MAX_SIZE;
		/**
		 * 上传图片域名<br/>
		 * 如果未指定将指向当前web服务器域名
		 */
		private String picDomain;
		/**
		 * 上传图片域名对应在服务器中的路径<br/>
		 * 如果未指定将指向当前web服务所在的路径
		 */
		private String picDomainWebRootPath;
		/**
		 * 图片缩放参数<br/>
		 * 如果缩放配置和剪切配置都设置了则以最后设置的为准(两中配置不能同时存在)
		 */
		private ZoomParam zoomParam;
		/**
		 * 图片剪切参数<br/>
		 * 如果缩放配置和剪切配置都设置了则以最后设置的为准(两中配置不能同时存在)
		 */
		private CutParam cutParam;
		
		public UploadParam(HttpServletRequest request, String fileParamter) {
			this.request = request;
			this.fileParamter = fileParamter; 
		}
		
		

		public String getPathPrefix() {
			return pathPrefix;
		}

		public void setPathPrefix(String pathPrefix) {
			this.pathPrefix = pathPrefix.replaceAll(FILE_SEPARATOR+"+", FILE_SEPARATOR);
		}
		
		public ImageUtils.ImageType[] getIncludeImageTypes() {
			return includeImageTypes;
		}

		public void setIncludeImageTypes(ImageUtils.ImageType[] includeImageTypes) {
			this.includeImageTypes = includeImageTypes;
		}

		public long getImageMaxSize() {
			return imageMaxSize;
		}

		public void setImageMaxSize(long imageMaxSize) {
			this.imageMaxSize = imageMaxSize;
		}

		public String getPicDomain() {
			return picDomain;
		}

		public void setPicDomain(String picDomain) {
			this.picDomain = picDomain;
		}

		public String getPicDomainWebRootPath() {
			return picDomainWebRootPath;
		}

		public void setPicDomainWebRootPath(String picDomainWebRootPath) {
			this.picDomainWebRootPath = picDomainWebRootPath.replaceAll(FILE_SEPARATOR+"+", FILE_SEPARATOR);
		}

		public HttpServletRequest getRequest() {
			return request;
		}

		public String getFileParamter() {
			return fileParamter;
		}
		public ZoomParam getZoomParam() {
			return zoomParam;
		}

		public void setZoomParam(ZoomParam zoomParam) {
			this.zoomParam = zoomParam;
			this.cutParam = null;
			
		}
		public CutParam getCutParam() {
			return cutParam;
		}

		public void setCutParam(CutParam cutParam) {
			this.cutParam = cutParam;
			this.zoomParam = null;
		}

		
	}
	/**
	 * 图片元数据
	 * @author xu.jianguo
	 *
	 */
	public static class  PicMetadata  {
		/**
		 * 绝对路径
		 */
		private String absPath;
		/**
		 * 相对路径
		 */
		private String relativePath;
		/**
		 * URL访问地址
		 */
		private String url;
		/**
		 * 图片宽度
		 */
		private int width;
		/**
		 * 图片高度
		 */
		private int height;
		/**
		 * 图片类型
		 */
		private ImageUtils.ImageType imageType;
		
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		public int getHeight() {
			return height;
		}
		public void setHeight(int height) {
			this.height = height;
		}
		public ImageUtils.ImageType getImageType() {
			return imageType;
		}
		public void setImageType(ImageUtils.ImageType imageType) {
			this.imageType = imageType;
		}
		public String getAbsPath() {
			return absPath;
		}
		public void setAbsPath(String absPath) {
			this.absPath = absPath;
		}
		public String getRelativePath() {
			return relativePath;
		}
		public void setRelativePath(String relativePath) {
			this.relativePath = relativePath;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		
		public String getWebRoot() {
			return absPath.replaceAll(relativePath, "");
		}
		
		public String getPicDomain() {
			return this.url.replaceAll(relativePath, "");
		}
		
		/**
		 * 根据新的绝对路径重新计算图片元数据<br/>
		 * 返回新的图片元数据<br/>
		 * 如果新的绝对路径和原有路径对应的webroot不相同则抛出异常
		 * @param absPath
		 * @return
		 * @throws java.io.IOException
		 * @author  xu.jianguo
		 */
		public PicMetadata reCalPicMetadata(String absPath) throws IOException {
			if(!absPath.startsWith(getWebRoot())) {
				throw new IOException("新的绝对路径和原有路径对应的webroot不相同，无法解析");
			}
			BufferedImage image = ImageIO.read(new File(absPath));
			PicMetadata picMetadata = new PicMetadata();
			picMetadata.setAbsPath(absPath);
			picMetadata.setWidth(image.getWidth());
			picMetadata.setHeight(image.getHeight());
			picMetadata.setImageType(this.imageType);
			String relativePath = FILE_SEPARATOR+absPath.substring(this.getWebRoot().length());
			relativePath = relativePath.replaceAll(FILE_SEPARATOR+"+", FILE_SEPARATOR);
			picMetadata.setRelativePath(relativePath);
			picMetadata.setUrl(this.getPicDomain()+relativePath);
			return picMetadata;
		}
	}
	/**
	 * 图片缩放参数
	 * @author xu.jianguo
	 *
	 */
	public static class ZoomParam {
		/**
		 * 缩放的宽度
		 */
		private final int width;
		/**
		 * 缩放的高度
		 */
		private final int height;
		/**
		 * 填充的背景色<br/>
		 * 如果该值不为空表示非等比例缩放
		 */
		private Color blackGround;
		/**
		 * 是否是等比例缩放
		 */
		private boolean rate = true;
		
		public ZoomParam(int width, int height) {
			super();
			this.rate = true;
			this.width = width;
			this.height = height;
		}
		public Color getBlackGround() {
			return blackGround;
		}
		public void setBlackGround(Color blackGround) {
			this.blackGround = blackGround;
			this.rate = false;
		}
		public int getWidth() {
			return width;
		}
		public int getHeight() {
			return height;
		}
		public boolean isRate() {
			return rate;
		}
	}
	/**
	 * 图片剪切参数
	 * @author xu.jianguo
	 *
	 */
	public static class CutParam {
		private final ImageUtils.Point leftTopPoint;
		private final ImageUtils.Point rightBottomPoint;
		
		public CutParam(int leftTopX,int leftTopY,int rightBottomX,int rightBottomY) {
			this.leftTopPoint = new ImageUtils.Point(leftTopX, leftTopY);
			this.rightBottomPoint = new ImageUtils.Point(rightBottomX, rightBottomY);
		}

		public ImageUtils.Point getLeftTopPoint() {
			return leftTopPoint;
		}

		public ImageUtils.Point getRightBottomPoint() {
			return rightBottomPoint;
		}
		
	}
}
