package com.autoStock.exchange.request;

import com.autoStock.exchange.ExchangeController;
import com.autoStock.exchange.request.listener.RequestMarketOrderIdListener;

/**
 * @author Kevin Kowalewski
 *
 */
public class OrderIdProvider {
	private static OrderIdProvider orderIdProvider = new OrderIdProvider();
	private RequestMarketOrderIdListener requestMarketOrderIdListener; 
	
	public static OrderIdProvider getInstance() {
		return orderIdProvider;
	}
	
	public synchronized void getNextOrderId(RequestMarketOrderIdListener requestMarketOrderIdListener){
		this.requestMarketOrderIdListener = requestMarketOrderIdListener;
		
		ExchangeController.getIbExchangeInstance().getNextValidOrderId();
	}

	public void onIdReceived(int orderId) {
		if (requestMarketOrderIdListener != null){
			requestMarketOrderIdListener.completed(orderId);
		}
	}
}
