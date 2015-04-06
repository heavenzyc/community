/**
 * @(#)ConstantUtils.java
 *
 * Copyright 2010 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * description
 * 
 * @author jianguo.xu
 * @version 1.0,2010-10-15
 */
public class ConfigUtils {
	/**
	 * 
	 */
	private static String FIRST_CONFIG_FILE = "/config.properties";
	/**
	 * 
	 */
	private static String SECOND_CONFIG_FILE = "/conf/config.properties";
	/**
	 * 默认人的配置文件路径
	 */
	private static String DEFAULT_CONFIG_FILE = "/config.properties";

	private static final Log LOG = LogFactory.getLog(ConfigUtils.class);

	private static Map<String, String> propertiesMap;
	 
	static {
		initProperty();
	}
	/**
	 * 初始化配置文件(config.properties)<br/>
	 * config.properties配置文件读取路径有优先级为 /config.properties > /conf/config.properties > ./config.properties <br/>
	 * 如果在/config.properties 或 /conf/config.properties配置文件中未找到相应的配置项目，将会采用默认的配置，也就是当前路径下的 config.properties
	 * 
	 */
	private static void initProperty() {
		if (propertiesMap != null)
			return;
		loadDefaultProperty();
		loadCustomProperty();
	}
	
	private static void loadCustomProperty() {
		InputStream ins = null;
		Properties properties = null;
		try {
			ins = ConfigUtils.class.getResourceAsStream(FIRST_CONFIG_FILE);
			if(ins !=null) {
				properties = new Properties();
				properties.load(ins);
			}
			else {
				ins = ConfigUtils.class.getResourceAsStream(SECOND_CONFIG_FILE);
				if(ins !=null) {
					properties = new Properties();
					properties.load(ins);
				}
			}
		} catch (Exception e) {
			LOG.error(e);
			return;
		}
		if(properties == null)
			return;
		Set<Entry<Object, Object>> entrySet = properties.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			propertiesMap.put((String) entry.getKey(),
					((String) entry.getValue()).trim());
		}
	}
	
	
	
	private static void loadDefaultProperty() {
		InputStream ins = null;
		Properties properties = new Properties();
		try {
			ins = ConfigUtils.class.getResourceAsStream(DEFAULT_CONFIG_FILE);
			properties.load(ins);
			
		} catch (FileNotFoundException e) {
			LOG.info("file not found." + DEFAULT_CONFIG_FILE, e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOG.info(e);
			throw new RuntimeException(e);
		}
		propertiesMap = new HashMap<String, String>();
		Set<Entry<Object, Object>> entrySet = properties.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			propertiesMap.put((String) entry.getKey(),
					((String) entry.getValue()).trim());
		}
	}

	public static String getString(String proKey) {
		return propertiesMap.get(proKey);
	}
	
	public static String getString(String proKey,String defaultValue) {
		String value = propertiesMap.get(proKey);
		return StringUtils.isNullOrEmpty(value)?defaultValue:value;
	}

	private static final String getWebRoot() {

		URL url = ConfigUtils.class.getResource("/");
		String path = url.getPath();
		if (path.endsWith("/WEB-INF/classes/")) {
			int beginIndex = path.length() - "WEB-INF/classes/".length();
			return path.substring(0, beginIndex);
		}
		return path;
	}

	public static final int getInt(String key) {
		return Integer.parseInt(propertiesMap.get(key));
	}
	
	public static final long getLong(String key) {
		return Long.parseLong(propertiesMap.get(key));
	}
	
	private static final boolean getBoolean(String key,boolean defaultValue) {
		String str = getString(key,new Boolean(defaultValue).toString());
		if(str.equalsIgnoreCase("true")||str.equals("1")||str.equals("是")||str.equalsIgnoreCase("yes"))
			return Boolean.TRUE;
		return Boolean.FALSE;
	}
	 

	/**
	 * 得到网站地址
	 */
	public static final String WEBSITE = getString("web.site","");
	/**
	 * 得到网站域名
	 */
	public static final String DOMAIN = getString("web.domain");
	/**
	 * 网站的绝对路径
	 */
	public static final String WEB_ROOT = getWebRoot();
	/**
	 * 网站上传图片的路径
	 */
	public static final String UPLOAD_IMAGE_PATH = getString("upload.image.path","/upload_file/image");
  
	/**
	 * 定时任务启动运行开关
	 */
	public static final boolean QUARTZ_RUN_OPEN = getBoolean("quartz.run.open", true);
	/**
	 * 项目的基础包目录
	 */
	public static final String BASE_PACKAGE = getString("project.base.package","com");
	
	/**
	 * xls模板的路径
	 */
	public static final String XLS_TEMPLATE_PATH = getString("xls.template.path","/xls_template/");
	 
	/**
	 * JSON VIEW 是否是开发模式
	 */
	public static final boolean JSON_VIEW_DEV_MODE = getBoolean("json.view.dev.mode", false);
	 
}
