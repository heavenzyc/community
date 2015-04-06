/**
 * @(#)CategoryElement.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.chart.element;

import com.heaven.zyc.support.chart.AbstractElement;
import com.heaven.zyc.support.chart.Attribute;

import java.util.List;
import java.util.Set;

/**
 * description
 * @author  jianguo.xu
 * @version 1.0,2011-8-19
 */
public class StylesElement extends AbstractElement {
	private static final String elementName ="styles";
	public StylesElement() {
		super(elementName, null,null);
	}
	public StylesElement(List<AbstractElement> childrens) {
		super(elementName, childrens);
	}
	public StylesElement(Set<Attribute> attributies,
			List<AbstractElement> childrens) {
		super(elementName, attributies, childrens);
	}

	public StylesElement(Set<Attribute> attributies) {
		super(elementName, attributies);
	}
	
}
