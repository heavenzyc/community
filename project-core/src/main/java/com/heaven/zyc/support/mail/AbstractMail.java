/**
 * @(#)AbstractFreemarkerMail.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.mail;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * description
 * @author  jianguo.xu
 * @version 1.0,2011-12-2
 */
public abstract class AbstractMail implements FreemarkerMail,SimpleMail{
	
	/**
	 * 邮件发送者接口
	 */
	protected JavaMailSender javaMailSender;

	/**
	 * freemarkder 路径
	 */
	protected String freeMarkerTemplatePathPrefix;
	/**
	 * freeMarker模板文件名
	 */
	protected String freeMarkerTemplateName;
	/**
	 * 邮件主题
	 */
	protected String subject;
	
	/**
	 * 发件人邮箱
	 */
	protected String fromMail;
	/**
	 * 发件人名
	 */
	protected String fromTitle;
	
	/**
	 * 定义 mail header属性配置
	 */
	protected Properties mailHeaders;
	/**
	 * 邮件编码
	 */
	protected String charSet;
	
	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}
	 
	public void setMailHeaders(Properties mailHeaders) {
		this.mailHeaders = mailHeaders;
	}
	 
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	
	public void setFreeMarkerTemplatePathPrefix(String freeMarkerTemplatePathPrefix) {
		this.freeMarkerTemplatePathPrefix = freeMarkerTemplatePathPrefix;
	}

	public void setFreeMarkerTemplateName(String freeMarkerTemplateName) {
		this.freeMarkerTemplateName = freeMarkerTemplateName;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setFromMail(String fromMail) {
		this.fromMail = fromMail;
	}
	public void setFromTitle(String fromTitle) {
		this.fromTitle = fromTitle;
	}
	
	@Override
	public void sendMail(String from, String to, String subject, String content) {
		sendMail(from, new String[] {to}, subject, content);
	}
	
	
	@Override
	public void sendMail(String to,
			Map<String, Object> freeMarkerModel) {
		sendMail(renderNameAndMail(fromTitle, fromMail), to, subject, renderFreeMarkerToText(freeMarkerModel));
	}
	
	@Override
	public void sendMail(String[] to,
			Map<String, Object> freeMarkerModel) {
		sendMail(renderNameAndMail(fromTitle, fromMail), to, subject, renderFreeMarkerToText(freeMarkerModel));
	}
	
	
	@Override
	public void sendMail(String nameOfTo, String emailOfTo,
			Map<String, Object> freeMarkerModel) {
		sendMail(fromTitle, fromMail,nameOfTo, emailOfTo, subject, renderFreeMarkerToText(freeMarkerModel));
		
	}
	@Override
	public void sendMail(String to,
			Map<String, Object> freeMarkerModel, List<File> files) {
		sendMail(renderNameAndMail(fromTitle, fromMail), to, subject, renderFreeMarkerToText(freeMarkerModel),files);
	}
	@Override
	public void sendMail(String from, String to, String subject,String content, List<File> files) {
		sendMail(from, new String[]{to}, subject, content,files);
	}
	@Override
	public void sendMail(String[] to, Map<String,Object> freeMarkerModel,List<File> files) {
		sendMail(renderNameAndMail(fromTitle, fromMail), to, subject, renderFreeMarkerToText(freeMarkerModel),files);
	}
	
	@Override
	public void sendMail(String nameOfTo, String emailOfTo,
			Map<String, Object> freeMarkerModel, List<File> files) {
		sendMail(renderNameAndMail(fromTitle, fromMail), renderNameAndMail(nameOfTo, emailOfTo), subject, renderFreeMarkerToText(freeMarkerModel),files);
	}
	@Override
	public void sendMail(String to,
			Map<String, Object> freeMarkerModel,
			Map<String, InputStream> inputStreams) {
		sendMail(new String[]{to},freeMarkerModel,inputStreams);
	}
	@Override
	public void sendMail(String[] to, Map<String,Object> freeMarkerModel,Map<String, InputStream> inputStreams) {
		sendMail(renderNameAndMail(fromTitle, fromMail), to, subject, renderFreeMarkerToText(freeMarkerModel),inputStreams);
	}
	@Override
	public void sendMail(String from, String to, String subject,
			String content, Map<String, InputStream> inputStreams) {
		sendMail(from, new String[]{to}, subject,
				content, inputStreams);
	}
	
	
	@Override
	public void sendMail(String nameOfTo, String emailOfTo,
			Map<String, Object> freeMarkerModel,
			Map<String, InputStream> inputStreams) {
		sendMail(renderNameAndMail(fromTitle, fromMail), renderNameAndMail(nameOfTo, emailOfTo), subject, renderFreeMarkerToText(freeMarkerModel),inputStreams);
	}
	
	/**
	 * 根据freemarker model生成邮件正文
	 * @author jianguo.xu
	 * @param model
	 * @return
	 */
	private String renderFreeMarkerToText(Map<String,Object> model){
		Configuration configuration = new Configuration();
		configuration.setDefaultEncoding(charSet);
		configuration.setClassForTemplateLoading(AbstractMail.class, freeMarkerTemplatePathPrefix);
		configuration.setObjectWrapper(new DefaultObjectWrapper());
		try {
			Template temp = configuration.getTemplate(freeMarkerTemplateName);
			try {
				return FreeMarkerTemplateUtils.processTemplateIntoString(temp, model);
			} catch (TemplateException e) {
				throw new RuntimeException("process freemarker error.",e);
			}
		} catch (IOException e) {
			throw new RuntimeException("process freemarker error.",e);
		}
		 
	}
	
	/**
	 * mail编码
	 * @author jianguo.xu
	 * @param value
	 * @return
	 */
	protected String  encodeText(String value) {
		 
		try {
			//return MimeUtility.encodeText(value, "GBK", "Q");
			return MimeUtility.encodeText(value);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
			 
		} 
	}
	
	/**
	 * 构建发件人或发件人
	 * @author jianguo.xu
	 * @return
	 */
	protected String renderNameAndMail(String name,String mail) {
		return encodeText(name)+"<" + mail + ">";
	}
 
}
