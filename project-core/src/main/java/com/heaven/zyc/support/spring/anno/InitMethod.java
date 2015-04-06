/**
 * @(#)Init.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.spring.anno;

import java.lang.annotation.*;

/**
 * 配置有该注解的方法在系统启动后将被调用 <br/>
 * 如果是是非静态方法，则方法对应的类必须有默认构造函数<br/>
 * 方法上不能带有参数，否则将被忽略掉<br/>
 * @author  jianguo.xu
 * @version 1.0,2011-7-29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InitMethod {
	boolean isStatic();
}
