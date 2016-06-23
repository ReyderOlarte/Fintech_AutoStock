/**
 * 
 */
package com.autoStock.tools;

import com.autoStock.Co;

/**
 * @author Kevin Kowalewski
 *
 */
public class ThreadTools {
	public static StackTraceElement[] getStackTrace(){
		return Thread.currentThread().getStackTrace();
	}
	
	public static void printStackTrace(){
		for (StackTraceElement element : getStackTrace()){
			Co.println(element.toString());
		}
	}
}
