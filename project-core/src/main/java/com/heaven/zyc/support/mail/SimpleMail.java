/**
 * @(#)SimpleMailS.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.mail;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * description
 * 
 * @author jianguo.xu
 * @version 1.0,2011-12-2
 */
public interface SimpleMail {

	/**
	 * 发送邮件
	 * @author jianguo.xu
	 * @param from		发件人 
	 * @param to		收件人
	 * @param subject	主题
	 * @param content	邮件正文
	 */
	public void sendMail(String from, String to, String subject, String content);
	
	public void sendMail(String from, String[] to, String subject, String content);
	
	
	/**
 	 * 发送邮件<br/>
 	 * 发件人的名字和邮箱地址分开<br/>
 	 * 收件人的名字和邮箱地址分开<br/>
 	 * @author jianguo.xu
 	 * @param nameOfFrom	发件人名字
 	 * @param mailOfFrom	发件人邮箱
 	 * @param nameOfTo		收件人名字
 	 * @param mailOfTo		收件人邮箱
 	 * @param subject		主题
 	 * @param content		邮件正文
 	 */
	public void sendMail(String nameOfFrom, String mailOfFrom, String nameOfTo, String mailOfTo, String subject, String content) ;
	
	
	/**
	 * 发送邮件<br/>
	 * 可发送附件<br/>
	 * @author jianguo.xu
	 * @param from		发件人 
	 * @param to		收件人
	 * @param subject	主题
	 * @param content	邮件正文
	 * @param files 	邮件附件
	 */
	public void sendMail(String from, String to, String subject,
                         String content, List<File> files);
	public void sendMail(String from, String[] to, String subject,
                         String content, List<File> files);
	
	
	
	/**
	 * 发送邮件<br/>
	 * 以流的形式发送附件<br/>
	 * @author jianguo.xu
	 * @param from		发件人 
	 * @param to		收件人
	 * @param subject	主题
	 * @param content	邮件正文
	 * @param inputStreams		邮件附件 key:附件名称 value:附件内容
	 */
	public void sendMail(String from, String to, String subject,
                         String content, Map<String, InputStream> inputStreams);
	
	public void sendMail(String from, String[] to, String subject,
                         String content, Map<String, InputStream> inputStreams);
	
	/**
 	 * 发送邮件<br/>
 	 * 发件人的名字和邮箱地址分开<br/>
 	 * 收件人的名字和邮箱地址分开<br/>
 	 * 可发送附件<br/>
 	 * @author jianguo.xu
 	 * @param nameOfFrom	发件人名字
 	 * @param mailOfFrom	发件人邮箱
 	 * @param nameOfTo		收件人名字
 	 * @param mailOfTo		收件人邮箱
 	 * @param subject		主题
 	 * @param content		邮件正文
 	 * @param files 	邮件附件
 	 */
	public void sendMail(String nameOfFrom, String mailOfFrom, String nameOfTo, String mailOfTo, String subject, String content, List<File> files) ;
	
	/**
 	 * 发送邮件<br/>
 	 * 发件人的名字和邮箱地址分开<br/>
 	 * 收件人的名字和邮箱地址分开<br/>
 	 * 以流的形式发送附件<br/>
 	 * @author jianguo.xu
 	 * @param nameOfFrom	发件人名字
 	 * @param mailOfFrom	发件人邮箱
 	 * @param nameOfTo		收件人名字
 	 * @param mailOfTo		收件人邮箱
 	 * @param subject		主题
 	 * @param content		邮件正文
 	 * @param inputStreams		邮件附件 key:附件名称 value:附件内容
 	 */
	public void sendMail(String nameOfFrom, String mailOfFrom, String nameOfTo, String mailOfTo, String subject, String content, Map<String, InputStream> inputStreams);
}
