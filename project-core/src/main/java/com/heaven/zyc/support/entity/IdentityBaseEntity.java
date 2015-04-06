/**
 * @(#)IdentityBaseEntity.java
 * Copyright 2013 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author  xu.jianguo
 * @date  2013-1-28
 * description
 */
@MappedSuperclass
public abstract class IdentityBaseEntity extends BaseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer id;
}
