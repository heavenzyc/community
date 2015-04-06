/**
 * @(#)XmlUtils.java
 *
 * Copyright 2010 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.utils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * description
 * 
 * @author jianguo.xu
 * @version 1.0,2010-10-8
 */
public final class XmlUtils {

	public static Document createDom(Element root) {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("GBK");
		document.add(root);
		return document;
	}

	/**
	 * 给父节点添加子节点
	 * 
	 * @author jianguo.xu
	 * @param parent
	 * @param elementName
	 * @param value
	 */
	public static void addChild(Element parent, String elementName, String value) {
		Element element = parent.element(elementName);
		if (element == null) {
			element = parent.addElement(elementName);
		}
		value = value == null ? "" : value;
		element.setText(value);
	}

	public static void addChild(Element parent, String elementName, Object value) {
		addChild(parent, elementName, String.valueOf(value));
	}
	
    /**
     *elementName自动转换为大写节点Name 
     * @author MrXu
     * @param parent
     * @param elementName
     * @param value
     */
	public static void addChildUp(Element parent, String elementName, Object value) {
		if(value==null) return;
		addChild(parent, elementName.toUpperCase(), String.valueOf(value));
	}

	/**
	 * 给父节点添加子节点 节点类容定义在CDATA中
	 * 
	 * @author jianguo.xu
	 * @param parent
	 * @param elementName
	 * @param value
	 */
	public static void addChildByCDATA(Element parent, String elementName, String value) {
		Element element = parent.element(elementName);
		if (element == null) {
			element = parent.addElement(elementName);
		}
		value = value == null ? "" : value;
		element.addCDATA(value);
	}

	/**
	 * 自动转换大写节点名称
	 * 得到parentElement 节点下子节点的值
	 * eg.<HEAER><MCODE>123</MCODE><TYPE>321</TYPE></HEADER> Element parent =
	 * new DOMElement("HEAER"); String value =
	 * getSubElementValue(parent,"MCODE"); value = 123
	 * 
	 * @author jianguo.xu
	 * @param parentElement
	 * @param queryName
	 * @return
	 */
	public static String getSubElementValueUp(Element parentElement, String queryName) {
		return getSubElementValue(parentElement, queryName.toUpperCase());
	}

	public static String getSubElementValue(Element parentElement, String queryName) {
		Element element = parentElement.element(queryName);
		if (element == null)
			return null;
		String value = element.getTextTrim();
		return value == null || value.equals("") ? null : value;
	}
	 
}
