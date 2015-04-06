package com.heaven.zyc.support.spring.view.support;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.URL;
import java.util.*;


public class JsonConfigHandler {
	private static final Log LOG = LogFactory.getLog(JsonConfigHandler.class);
	private String jsconConfigPath;
	private static boolean devMode;
	/**
	 * JSON 配置文件map映射
	 * key:json资源文件
	 * value:最后修改时间
	 */
	private static Map<File, Long> resourcesMap;
	private static Map<String, JsonConfig> jsonMap ;
	
	
	public void setJsconConfigPath(String jsconConfigPath) {
		this.jsconConfigPath = jsconConfigPath;
	}
	

	public JsonConfigHandler(String jsconConfigPath) {
		this(jsconConfigPath, false);
	}

	@SuppressWarnings("static-access")
	public JsonConfigHandler(String jsconConfigPath,boolean devMode) {
		this.jsconConfigPath = jsconConfigPath;
		this.devMode = devMode;
	}


	/**
	 * 初始化json配置
	 * @author jianguo.xu
	 */
	public void init() {
	 
		jsonMap = new HashMap<String, JsonConfig>();
		Set<File> resources = getResources();
		for(File resource : resources) {
			parserJsonItem(resource,jsonMap,true);
		}
		createResourcesMap(resources);
		LOG.info("init json config success.");
	}
	/**
	 * 创建资源文件的映射
	 * @param resources
	 */
	private  void createResourcesMap(Set<File> resources) {
		resourcesMap = new HashMap<File, Long>();
		for(File resource : resources) {
			resourcesMap.put(resource, resource.lastModified());
		}
	}
	
	/**
	 * 解析json xml
	 * @author jianguo.xu
	 * @param resource json 文件
	 * @param jsonMap
	 * @param checkUnique  是否检查json配置项的唯一性质
	 */
	private static void parserJsonItem(File resource,Map<String, JsonConfig> jsonMap,boolean checkUnique) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(resource);
		} catch (DocumentException e) {
			LOG.error("not find the xml file,the path is error.",e);
			return;
		}
		Element root = doc.getRootElement();
		if (root != null) {
			for (Iterator<?> i = root.elementIterator("item"); i.hasNext();) {
				Element ItemNote = (Element) i.next();
				String name = ItemNote.attributeValue("name").trim();
				JsonConfig json = getParamNote(ItemNote);
				putJson(name,json,jsonMap,checkUnique);
			}
		}
	}
	
	private static void putJson(String name,JsonConfig json,Map<String, JsonConfig> jsonMap,boolean checkUnique) {
		if (StringUtils.isBlank(name)) return;
		if(checkUnique&&jsonMap.get(name)!=null)
			throw new RuntimeException("json config name is not unique : "+name);
		jsonMap.put(name, json);
		 
	}
	
	private  File loadRootJsonConfig() {
		try {
			String path = JsonConfigHandler.class.getResource(jsconConfigPath).getPath();
			return new File(path);
		}
		catch (NullPointerException e) {
			LOG.warn(jsconConfigPath+" not found.");
			return null;
		}
		
		
	}
	
	private  Set<File> getResources() {
		Set<File> resources = new HashSet<File>();
		File rootFile = loadRootJsonConfig();
		if(rootFile == null) return resources;
		resources.add(rootFile);
		SAXReader reader = new SAXReader();		 
		Document doc = null;
		try {
			doc = reader.read(rootFile);
		} catch (DocumentException e) {
			LOG.error("not find the xml file,the path is error.",e);
		}
		Element root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> importElements = root.selectNodes("/json/import");
		if(importElements == null||importElements.size() == 0) return resources;
		for(Element element : importElements) {
			String resource = element.attributeValue("resource");
			URL url =  JsonConfigHandler.class.getResource(resource);
			if(url ==null) {
				throw new RuntimeException("json config file not found : "+resource);
			}
			String itemAbsPath = url.getPath();
			resources.add(new File(itemAbsPath));
		}
		return resources;
	}

	private static JsonConfig getParamNote(Element ItemNote) {
		Map<String, String> ParamMap = new HashMap<String, String>();
		for (Iterator<?> i = ItemNote.elementIterator("param"); i.hasNext();) {
			Element ParamNote = (Element) i.next();
			String name = ParamNote.attributeValue("name").trim();
			if (StringUtils.isBlank(name)) {
				continue;
			}
			String value = ParamNote.getTextTrim().trim();
			ParamMap.put(name, value);
		}
		JsonConfig json = new JsonConfig();
		if(ParamMap.get("includeProperties")!=null) {
			json.setIncludeProperties(ParamMap.get("includeProperties").trim());
		}
		if(ParamMap.get("excludeProperties")!=null) {
			json.setExcludeProperties(ParamMap.get("excludeProperties").trim());
		}
		if(ParamMap.get("ignoreHierarchy")!=null) {
			if (StringUtils.equals(ParamMap.get("ignoreHierarchy").trim(), "false")) {
				json.setIgnoreHierarchy(false);
			}
		}
		if(ParamMap.get("enumAsBean")!=null) {
			if (StringUtils.equals(ParamMap.get("enumAsBean").trim(), "false")) {
				json.setEnumAsBean(false);
			}
		}
		if(ParamMap.get("excludeNullProperties")!=null) {
			if (StringUtils.equals(ParamMap.get("excludeNullProperties").trim(), "true")) {
				json.setIgnoreHierarchy(true);
			}
		}
		return json;

	}
    /**
     * 根据key取得对象<br/>
     * 如果不存在则返回null
     * @param key
     * @return
     */
	public static JsonConfig getConfig(String key) {
		reloadJsonConfig();
		return jsonMap.get(key.trim());
		 
	}
	
	/**
	 * 如果是开发模式则重新加载
	 * @author jianguo.xu
	 */
	private static void reloadJsonConfig() {
		if(!devMode)
			return;
		for(File file : resourcesMap.keySet()) {
			long oldModifiedTime = resourcesMap.get(file);
			long currentModifiedTime =  file.lastModified();
			if(oldModifiedTime==currentModifiedTime)
				continue;
			LOG.info("reload json config : "+file.getName());
			parserJsonItem(file, jsonMap, false);
			resourcesMap.put(file, currentModifiedTime);
		}
	}
	public static class JsonConfig {
		/** 包括属性 */
		private String includeProperties;
		/** 排除属性 */
		private String excludeProperties;
		/** 忽略层级关系,默认是true */
		private boolean ignoreHierarchy;
		/** 枚举是否转换成对象，默认是:true */
		private boolean enumAsBean;
		/** 排除null属性 */
		private boolean excludeNullProperties;

		public JsonConfig() {
			this.ignoreHierarchy = true;
			this.enumAsBean = true;
			this.excludeNullProperties = false;

		}

		public String getIncludeProperties() {
			return includeProperties;
		}

		@Override
		public String toString() {
			return "JsonConfig [includeProperties=" + includeProperties + ", excludeProperties=" + excludeProperties
					+ ", ignoreHierarchy=" + ignoreHierarchy + ", enumAsBean=" + enumAsBean + ", excludeNullProperties="
					+ excludeNullProperties + "]";
		}

		public boolean isExcludeNullProperties() {
			return excludeNullProperties;
		}

		public void setExcludeNullProperties(boolean excludeNullProperties) {
			this.excludeNullProperties = excludeNullProperties;
		}

		public void setIncludeProperties(String includeProperties) {
			this.includeProperties = includeProperties;
		}

		public String getExcludeProperties() {
			return excludeProperties;
		}

		public void setExcludeProperties(String excludeProperties) {
			this.excludeProperties = excludeProperties;
		}

		public boolean isIgnoreHierarchy() {
			return ignoreHierarchy;
		}

		public void setIgnoreHierarchy(boolean ignoreHierarchy) {
			this.ignoreHierarchy = ignoreHierarchy;
		}

		public boolean isEnumAsBean() {
			return enumAsBean;
		}

		public void setEnumAsBean(boolean enumAsBean) {
			this.enumAsBean = enumAsBean;
		}
	}
}
