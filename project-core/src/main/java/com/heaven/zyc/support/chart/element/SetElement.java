/**
 * @(#)SetElement.java
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
public class SetElement extends AbstractElement {
	private static final String elementName ="set";
	public SetElement() {
		super(elementName, null,null);
	}
	public SetElement( List<AbstractElement> childrens) {
		super(elementName, childrens);
	}

	public SetElement( Set<Attribute> attributies,
			List<AbstractElement> childrens) {
		super(elementName, attributies, childrens);
	}

	public SetElement( Set<Attribute> attributies) {
		super(elementName, attributies);
	}
 

}
