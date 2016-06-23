package com.autoStock.internal;

import java.lang.Thread.UncaughtExceptionHandler;

import com.autoStock.Co;

/**
 * @author Kevin Kowalewski
 *
 */
public class CustomUncaughtExceptionHandler implements UncaughtExceptionHandler {
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		Co.println("***************************************** JUST CRASHED *****************************************");
		Thread.getDefaultUncaughtExceptionHandler().uncaughtException(thread, throwable);
	}
}
