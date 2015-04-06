/**
 * @(#)FreeMarkerPaginationConvertor.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.pagination;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * description
 * @author  jianguo.xu
 * @version 1.0,2011-3-11
 */
public class FreeMarkerPaginationConvertor implements PaginationConvertor{
	//private static final String PAGINATION_TEMPLATE="pagination.ftl";
	 private static final String PAGINATION_TEMPLATE="pagination_one.ftl";
	private static final String TEMPLATE_ENCODING="UTF-8";
	private static final Log LOG = LogFactory.getLog(FreeMarkerPaginationConvertor.class);
	
	private static Template template;
	static {
		template = getTemplate();
	}
	
	private static Template getTemplate() {
		/*URL url = FreeMarkerPaginationConvertor.class.getResource("");
		File dir = new File(url.getPath());*/
		Configuration freemarkerConfiguration = new Configuration();
		freemarkerConfiguration.setDefaultEncoding(TEMPLATE_ENCODING);
		try {
			//freemarkerConfiguration.setDirectoryForTemplateLoading(dir);
			freemarkerConfiguration.setClassForTemplateLoading(FreeMarkerPaginationConvertor.class, "/ftl");
			 
			 Template temp = freemarkerConfiguration.getTemplate(PAGINATION_TEMPLATE);
			 return temp;
		} catch (IOException e) {
			throw new RuntimeException("get pagination template error.",e);
		}
	}
	

	public String convert(Pagination pagination) {
		 
		 Map<String,Object> model = new HashMap<String, Object>();
		 model.put("pagination", pagination);
		 try {
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}
}
