/**
 * 
 */
package com.autoStock.exchange.request.listener;

import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.results.ExResultHistoricalData.ExResultSetHistoricalData;

/**
 * @author Kevin Kowalewski
 *
 */
public interface RequestHistoricalDataListener extends RequestListenerBase {
	public void completed(RequestHolder requestHolder, ExResultSetHistoricalData exResultSetHistoricalData);
}
