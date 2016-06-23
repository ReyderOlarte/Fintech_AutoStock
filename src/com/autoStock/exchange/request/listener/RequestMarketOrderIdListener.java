/**
 * 
 */
package com.autoStock.exchange.request.listener;


/**
 * @author Kevin Kowalewski
 * 
 */
public interface RequestMarketOrderIdListener extends RequestListenerBase {
	public void completed(int orderId);
}
