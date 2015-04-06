/**
 * @(#)ExecutorServiceUtil.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.support.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * JDK 1.6新的并发工具类
 * @author  jianguo.xu
 * @version 1.0,2011-9-11
 */
public class ExecutorServiceUtil {
	
	private static int fixedThreadPoolSize = 10;
	private static ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private static ExecutorService fixedThreadPoolService = Executors.newFixedThreadPool(fixedThreadPoolSize);
	/**
	 * 用单个 worker 线程的 Executor来执行任务
	 * @author jianguo.xu
	 * @param command
	 */
	public static void execute(Runnable command) {
		executorService.execute(command);
	 
	}
	/**
	 * 用可重用固定线程数的线程池来执行任务
	 * @author jianguo.xu
	 * @param command
	 */
	public static void executeInThreadPool(Runnable command) {
		fixedThreadPoolService.execute(command);
	 
	}
	
	
	 
}
