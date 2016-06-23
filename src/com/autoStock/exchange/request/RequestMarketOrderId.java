/**
 * 
 */
package com.autoStock.exchange.request;

import com.autoStock.exchange.ExchangeController;

/**
 * @author Kevin Kowalewski
 *
 */
public class RequestMarketOrderId {
	private Thread threadForExecution;
	
	public void execute(){
		threadForExecution = new Thread(new Runnable(){
			@Override
			public void run() {
				ExchangeController.getIbExchangeInstance().getNextValidOrderId();	
			}
		});
		
		threadForExecution.start();
	}
	
	public void cancel(){
		try {threadForExecution.interrupt();}catch(Exception e){}
	}
}
