/**
 * 
 */
package com.autoStock;

import com.autoStock.internal.ApplicationStates;
import com.autoStock.internal.Global;
import com.autoStock.r.RServeController;

/**
 * @author Kevin
 *
 */
public class MainRTest {
	private RServeController rServer = new RServeController();
	
	public MainRTest() {
		Global.callbackLock.requestLock();
		rServer.start();
	}

	public void run(){
		
	}
}
