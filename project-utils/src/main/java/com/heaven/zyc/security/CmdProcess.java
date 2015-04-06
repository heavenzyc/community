/**
 * @(#)CmdProcess.java
 *
 * Copyright 2010 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * description
 * @author  jianguo.xu
 * @version 1.0,2010-12-1
 */
public class CmdProcess {
	private static final Log LOG = LogFactory.getLog(CmdProcess.class);
	private String cmdString;
	private int timeout;
	private Process process;
	
	private static final String DEFAULT_ENCODE="GBK";
	
	private String encode;
	
	public CmdProcess(String cmdString, int timeout) {
		this(cmdString, timeout, DEFAULT_ENCODE);
	}
	
	public CmdProcess(String cmdString, int timeout,String encode) {
		this.cmdString = cmdString;
		this.timeout = timeout;
		this.encode = encode;
	}

	public int execute() {
		int status=-1;
		Runtime runtime = Runtime.getRuntime();
		try {
			 process = runtime.exec(cmdString);
			 LOG.info("exec : "+cmdString);
			TimeoutThread timeoutThread = new TimeoutThread(process);
			timeoutThread.start();
			doExecute();
			status = process.waitFor();
			timeoutThread.cancel();
			
		} catch (IOException e) {
			LOG.error(e);
		} catch (InterruptedException e) {
			LOG.error(e);
		}
		return status;
	}
	private void doExecute() {
		InputStream err = process.getErrorStream();
		InputStream in = process.getInputStream();
		BufferedReader errReader = null;
		BufferedReader inReader = null;
		try {
			errReader = new BufferedReader(new InputStreamReader(err,encode));
			inReader = new BufferedReader(new InputStreamReader(in,encode));
		} catch (UnsupportedEncodingException e) {
			LOG.error(e);
			throw new RuntimeException(e);
			
		}

		String inLine = null;
		String errLine = null;
		try {
			while ((inLine = inReader.readLine()) != null) {
				LOG.info(inLine);
			}
			while ((errLine = errReader.readLine()) != null) {
				LOG.error(errLine);
			}
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	
	private class TimeoutThread extends Thread{
		private Process process;
		 
		private boolean cancel;
		private TimeoutThread(Process process) {
			super();
			this.process = process;
			 
			this.setDaemon(true);
		}
		public synchronized void cancel() {
			cancel = true;
		}
		@Override
		public void run() {
			try {
				Thread.sleep(timeout);
				if(!cancel) process.destroy();
			} catch (InterruptedException e) {
				LOG.error(e);
			}
		}
		
	}
}

