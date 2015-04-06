/**
 * @(#)FreemarkerMail.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.mail;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 通过freemaker发送邮件
 * @author  jianguo.xu
 * @version 1.0,2011-12-2
 */
public interface FreemarkerMail {
	/**
	 * 发送邮件
	 * @author jianguo.xu
	 * @param to	收件人
	 * @param freeMarkerModel
	 */
	public void sendMail(String to, Map<String, Object> freeMarkerModel) ;
	
	
	/**
	 * 群发发送邮件
	 * @author jianguo.xu
	 * @param to	收件人
	 * @param freeMarkerModel
	 */
	public void sendMail(String[] to, Map<String, Object> freeMarkerModel) ;
	
	/**
	 * 发送邮件
	 * @author jianguo.xu
	 * @param nameOfTo	收件人名字
	 * @param emailOfTo	收件人邮箱
	 * @param freeMarkerModel
	 */
	public void sendMail(String nameOfTo, String emailOfTo, Map<String, Object> freeMarkerModel);
	
	/**
	 * 发送邮件<br/>
	 * 可发送附件<br/>
	 * @author jianguo.xu
	 * @param to	收件人
	 * @param freeMarkerModel
	 */
	public void sendMail(String to, Map<String, Object> freeMarkerModel, List<File> files) ;
	public void sendMail(String[] to, Map<String, Object> freeMarkerModel, List<File> files) ;
	/**
	 * 发送邮件<br/>
	 * 可发送附件<br/>
	 * @author jianguo.xu
	 * @param nameOfTo	收件人名字
	 * @param emailOfTo	收件人邮箱
	 * @param freeMarkerModel
	 */
	public void sendMail(String nameOfTo, String emailOfTo, Map<String, Object> freeMarkerModel, List<File> files);
	
	
	/**
	 * 发送邮件<br/>
	 * 以流的形式发送附件<br/>
	 * @author jianguo.xu
	 * @param to	收件人
	 * @param freeMarkerModel
	 * @param inputStreams		邮件附件 key:附件名称 value:附件内容
	 */
	public void sendMail(String to, Map<String, Object> freeMarkerModel, Map<String, InputStream> inputStreams) ;
	
	public void sendMail(String[] to, Map<String, Object> freeMarkerModel, Map<String, InputStream> inputStreams) ;
	
	/**
	 * 发送邮件<br/>
	 * 以流的形式发送附件<br/>
	 * @author jianguo.xu
	 * @param nameOfTo	收件人名字
	 * @param emailOfTo	收件人邮箱
	 * @param freeMarkerModel
	 * @param inputStreams		邮件附件 key:附件名称 value:附件内容
	 */
	public void sendMail(String nameOfTo, String emailOfTo, Map<String, Object> freeMarkerModel, Map<String, InputStream> inputStreams);
	
	
	
}
