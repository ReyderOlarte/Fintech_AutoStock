/**
 * 
 */
package com.autoStock.internal;

/**
 * @author Kevin Kowalewski
 *
 */
public class Global {
	private static Mode mode;
	public static CallbackLock callbackLock = new CallbackLock();
	
	public static Mode getMode() {return mode;}
	public static void setMode(Mode mode) {Global.mode = mode;}

	public static enum Mode {
		server,
		client,
		client_skip_tws,
	}
	
	public static enum Brokerage {
		ib,
	}
}
