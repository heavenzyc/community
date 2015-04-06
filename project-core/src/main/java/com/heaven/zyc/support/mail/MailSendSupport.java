/**
 * @(#)FreemarkerMailSupport.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.mail;


import com.heaven.zyc.io.IOUtils;
import com.heaven.zyc.support.thread.ExecutorServiceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 通过freemarker模板发送邮件类
 * @author  jianguo.xu
 * @version 1.0,2011-3-9
 */
public class MailSendSupport extends AbstractMail implements JavaMailSender {
	
	private static final Log LOG = LogFactory
			.getLog(MailSendSupport.class);
	
	@Override
	public MimeMessage createMimeMessage() {
		return javaMailSender.createMimeMessage();
	}

	@Override
	public MimeMessage createMimeMessage(InputStream contentStream)
			throws MailException {
		return javaMailSender.createMimeMessage(contentStream);
	}
	
	protected void injectMailHeader(MimeMessage mm) {
		for(Object key: mailHeaders.keySet()) {
			String name = (String) key;
			try {
				mm.setHeader(name, (String) mailHeaders.getProperty(name));
			} catch (MessagingException e) {
				LOG.error(e);
			}
		}
	}
	 
	private void doSend(final Object object) {
		Runnable commend = new Runnable() {
			@Override
			public void run() {
				if (object instanceof SimpleMailMessage) {
					javaMailSender.send((SimpleMailMessage) object);
				} else if (object instanceof MimeMessage) {
					javaMailSender.send((MimeMessage) object);
				} else if (object instanceof MimeMessagePreparator) {
					javaMailSender.send((MimeMessagePreparator) object);
				}
			}
		};
		ExecutorServiceUtil.executeInThreadPool(commend);
	}
	@Deprecated
	@Override
	public void send(MimeMessage mimeMessage) throws MailException {
		//injectMailHeader(mimeMessage);
		doSend(mimeMessage);
	}
	@Deprecated
	@Override
	public void send(MimeMessage[] mimeMessages) throws MailException {
		for(MimeMessage mimeMessage :  mimeMessages) {
			send(mimeMessage);
		}
	}
	@Deprecated
	@Override
	public void send(MimeMessagePreparator mimeMessagePreparator)
			throws MailException {
		doSend(mimeMessagePreparator);
	}
	@Deprecated
	@Override
	public void send(MimeMessagePreparator[] mimeMessagePreparators)
			throws MailException {
		for(MimeMessagePreparator mimeMessagePreparator :  mimeMessagePreparators) {
			send(mimeMessagePreparator);
		}
	}
	@Deprecated
	@Override
	public void send(SimpleMailMessage simpleMessage) throws MailException {
		doSend(simpleMessage);
		
	}
	@Deprecated
	@Override
	public void send(SimpleMailMessage[] simpleMessages) throws MailException {
		for(SimpleMailMessage simpleMessage :  simpleMessages) {
			send(simpleMessage);
		}
	}
	/**
	 * 创建 MimeMessageHelper
	 * @author jianguo.xu
	 * @param from
	 * @param to
	 * @param subject
	 * @param content
	 * @param multipart
	 * @return
	 */
	private MimeMessageHelper createMimeMessageHelper(String from,String[] to,String subject,String content,boolean multipart) {
		MimeMessage mimeMessage = createMimeMessage();
		try {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,multipart,charSet);
			messageHelper.setSubject(subject);
			messageHelper.setFrom(from);
			messageHelper.setTo(to);
			messageHelper.setText(content,true);
			return messageHelper;
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} 
	}
	
	private void addFile(MimeMessageHelper mimeMessageHelper,List<File> files) {
		if(files == null||files.size() ==0) return;
		
		for(int i =0;i<files.size();i++) {
			File file = files.get(i);
			String fileName = file.getName();
			try {
				//mimeMessageHelper.addInline(encodeText(attachmentFilename), file);
				mimeMessageHelper.addAttachment(encodeText(fileName), file);
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void addInputStreame(MimeMessageHelper mimeMessageHelper,Map<String, InputStream> inputStreams) {
		 if(inputStreams == null||inputStreams.size() ==0) return;
		 for(String attachmentFilename : inputStreams.keySet()) {
			 InputStream ins = inputStreams.get(attachmentFilename); 
			 ByteArrayResource byteArrayResource = new ByteArrayResource(IOUtils.inputStreamToBytes(ins));
			 try {
				 mimeMessageHelper.addAttachment(encodeText(attachmentFilename), byteArrayResource);
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		 }
	}

	@Override
	public void sendMail(String from, String[] to, String subject,
			String content) {
		MimeMessageHelper mimeMessageHelper = createMimeMessageHelper(from, to, subject, content,false);
		send(mimeMessageHelper.getMimeMessage());
	}
	

	@Override
	public void sendMail(String nameOfFrom, String mailOfFrom, String nameOfTo,
			String mailOfTo, String subject, String content) {
		sendMail(renderNameAndMail(nameOfFrom, mailOfFrom), renderNameAndMail(nameOfTo, mailOfTo), subject, content);
	}

	@Override
	public void sendMail(String from, String[] to, String subject,
			String content, List<File> files) {
		MimeMessageHelper mimeMessageHelper = createMimeMessageHelper(from, to, subject, content,true);
		addFile(mimeMessageHelper, files);
		send(mimeMessageHelper.getMimeMessage());
	}
	

	@Override
	public void sendMail(String from, String[] to, String subject,
			String content, Map<String, InputStream> inputStreams) {
		MimeMessageHelper mimeMessageHelper = createMimeMessageHelper(from, to, subject, content,true);
		addInputStreame(mimeMessageHelper, inputStreams);
		send(mimeMessageHelper.getMimeMessage());
	}

	@Override
	public void sendMail(String nameOfFrom, String mailOfFrom, String nameOfTo,
			String mailOfTo, String subject, String content, List<File> files) {
		sendMail(renderNameAndMail(nameOfFrom, mailOfFrom), renderNameAndMail(nameOfTo, mailOfTo), subject, content, files);
	}

	@Override
	public void sendMail(String nameOfFrom, String mailOfFrom, String nameOfTo,
			String mailOfTo, String subject, String content,
			Map<String, InputStream> inputStreams) {
		sendMail(renderNameAndMail(nameOfFrom, mailOfFrom), renderNameAndMail(nameOfTo, mailOfTo), subject, content, inputStreams);
	}

	
	
	
}

