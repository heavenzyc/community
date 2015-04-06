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
public class TrendLinesElement extends AbstractElement {
	private static final String elementName ="trendLines";
	public TrendLinesElement() {
		super(elementName, null,null);
	}
	public TrendLinesElement(List<AbstractElement> childrens) {
		super(elementName, childrens);
	}
	public TrendLinesElement(Set<Attribute> attributies,
			List<AbstractElement> childrens) {
		super(elementName, attributies, childrens);
	}

	public TrendLinesElement(Set<Attribute> attributies) {
		super(elementName, attributies);
	}
	
}
