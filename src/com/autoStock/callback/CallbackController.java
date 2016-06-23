/**
 * 
 */
package com.autoStock.callback;

import java.util.ArrayList;

/**
 * @author Kevin Kowalewski
 *
 */
public class CallbackController {
	public static ArrayList<CallbackHolder> listOfCallbackHolder = new ArrayList<CallbackHolder>();
	
	public static void addCallback(CallbackHolder callbackHolder){
		listOfCallbackHolder.add(callbackHolder);
	}
	
	public static void removeCallback(CallbackHolder callbackHolder){
		
	}
}
