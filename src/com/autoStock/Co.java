package com.autoStock;

/**
 * @author Kevin Kowalewski
 *
 */
public class Co {
	public static enum Colors {
		green,
		red,
		blue,
		white,
		gray,
		black,
	}
	
	public static void print(String string){
		System.out.print(string);
	}
	
	public static void println(String string){
		System.out.println(string);
	}
	
	public static void log(String string){
		System.out.println(string);
	}
}
