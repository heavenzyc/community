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
public class ApplyElement extends AbstractElement {
    private static final String elementName ="apply";
	public ApplyElement() {
		super(elementName, null, null);
	}
	public ApplyElement(List<AbstractElement> childrens) {
		super(elementName, childrens);
	}
	

	public ApplyElement(Set<Attribute> attributies) {
		super(elementName, attributies);
	}
	public ApplyElement(Set<Attribute> attributies,
			List<AbstractElement> childrens) {
		super(elementName, attributies, childrens);
	}
}
