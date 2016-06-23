/**
 * 
 */
package com.autoStock;

/**
 * @author Kevin Kowalewski
 * 
 */
public class Log {
	public static void d(String string) {
		Co.println("DEBUG: " + string);
	}

	public static void i(String string) {
		Co.println("INFO: " + string);
	}

	public static void w(String string) {
		Co.println("WARNING: " + string);
	}

	public static void e(String string) {
		Co.println("ERROR: " + string);
	}

	public static void f(String string) {
		Co.println("FATAL ERROR: " + string);
	}
}
