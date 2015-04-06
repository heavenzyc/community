/**
 * @(#)FoodConfigUtils.java
 * Copyright 2013 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.config;

import com.heaven.zyc.utils.ConfigUtils;

/**
 * @author  xu.jianguo
 * @date  2013-3-29
 * description
 */
public class LocalConfigUtils extends ConfigUtils {
	/**
	 * 当前系统的域名
	 */
	public static final String WEB_SITE_DOMAIN = getString("web.site","");
	/**
	 * 得到网站地址
	 */
	public static final String STATIC_RESOURCE_DOMAIN = getString("static.resource.domain","");
	/**
	 * 餐饮业务系统内部上传图片，文件访问域名
	 */
	public static final String STATIC_PICUPLOAD_DOMAIN = getString("static.pic.domain","");

	public static final String STATIC_PICUPLOAD__ROOT_PATH = getString("static.pic.root.path","");

	/**
	 * 图片域名前缀
	 */
	public static final String IMAGE_SERVER_PATH = STATIC_PICUPLOAD_DOMAIN + UPLOAD_IMAGE_PATH;
	/**
	 * 图片存放地址前缀
	 */
	public static final String IMAGE_SERVER_FILEPATH  = STATIC_PICUPLOAD__ROOT_PATH + UPLOAD_IMAGE_PATH;
	/**
	 * 老系统图片，访问域名
	 */
	public static final String STATIC_OLD_SYSTEM_PIC_DOMAIN = getString("static.old.pic.domain","");

    /**
     * 老系统域名,商家系统域名
     */
	public static final String OLD_SYSTEM_DOMAIN = getString("old.system.domain","");
	/**
	 * 老系统静态图片根地址
	 */
	public static final String STATIC_OLD_SYSTEM_PICUPLOAD_ROOT_PATH = getString("static.old.pic.root.path","");
    /**
     * 婚庆wap页面域名
     */
    public static final String WAP_WEDDING_DOMAIN = getString("wap.wedding.domain");
    /**
     * 微信访问域名
     */
    public static final String WEIXIN_DOMAIN = getString("weixin.domain");

    /**
     * 微官网域名
     */
    public static final String WAP_SITE_DOMAIN = getString("merchant.wap.site.domain");
    /**
     * 是否为产品模式
     */
    public static final String IS_PRODUCT = getString("apple.push.switch");

    /**
     * 日志记录的JS路径
     */
    public static final String LOG_TRACKER_JS_URL = getString("logtracker.js.url","http://api.fc.pretang.com/logtracker/log.js");

    /**
     * 日志记录的gif请求地址
     */
    public static final String LOG_TRACKER_GIF_URL = getString("logtracker.gif.url","http://api.fc.pretang.com/logtracker/__log.gif");

	/**
	 * 构建餐饮系统图片的访问路径
	 * @param picPath
	 * @return
	 */
	public static String buildPicUrl(String picPath){
		return STATIC_PICUPLOAD_DOMAIN + picPath;
	}
	/**
	 * 老系统图片的访问路径,方便查找商户的图片地址
	 * @param picPath
	 * @return
	 */
	public static String buildOldSysPicUrl(String picPath){
		return STATIC_OLD_SYSTEM_PIC_DOMAIN + picPath;
	}
	/**
	 * 构造微信的帐号访问地址
	 * @param merchantid
	 * @return
	 */
	public static String buildWeixinDomain(String merchantid){
		return WEIXIN_DOMAIN + merchantid;
	}
}