/**
 * 
 */
package com.autoStock.exchange.request.listener;

import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.results.ExResultRealtimeData.ExResultSetRealtimeData;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 *
 */
public interface RequestRealtimeDataListener extends RequestListenerBase {
		public void receiveQuoteSlice(RequestHolder requestHolder, QuoteSlice typeQuoteSlice);
		public void completed(RequestHolder requestHolder, ExResultSetRealtimeData exResultSetRealtimeData);
}
