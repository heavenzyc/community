/**
 * @(#)CertificateUtils.java
 *
 * Copyright 2010 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * description
 * 
 * @author jianguo.xu
 * @version 1.0,2010-12-9
 */
public class CertificateUtils {

	private static final Log LOG = LogFactory.getLog(CertificateUtils.class);

	/**
	 * 验证证书是否过期或无效
	 * @param date
	 * @param certificate
	 * @return
	 */
	public static boolean verifyCertificate(Date date, Certificate certificate) {
		try {
			X509Certificate x509Certificate = (X509Certificate) certificate;
			x509Certificate.checkValidity(date);
			return true;
		} catch (Exception e) {
			LOG.debug("verify certificate error.", e);
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	/**
	 * 验证证书是否过期或无效
	 * @param date
	 * @param certificate
	 * @return
	 */
	public static boolean verifyCertificate(Certificate certificate) {
		return verifyCertificate(new Date(), certificate);
	}
	
	/**
	 * 从字节数组中加载X509证书
	 * @author jianguo.xu
	 * @param certBytes
	 * @return
	 */
	public static X509Certificate loadCertificate(byte[] certBytes){
		InputStream  ins =new ByteArrayInputStream(certBytes);;
		return loadCertificate(ins);
	}
	/**
	 * 从输入流中加载x509证书
	 * @author jianguo.xu
	 * @param ins
	 * @return
	 */
	public static X509Certificate loadCertificate(InputStream  ins){
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return (X509Certificate) cf.generateCertificate(ins);
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		}
		 finally {
			if(ins!=null) {
				try {
					ins.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
}
