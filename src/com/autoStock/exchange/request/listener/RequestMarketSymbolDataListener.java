/**
 * 
 */
package com.autoStock.exchange.request.listener;

import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.results.ExResultMarketSymbolData.ExResultSetMarketSymbolData;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 *
 */
public interface RequestMarketSymbolDataListener extends RequestListenerBase {
	public void receiveQuoteSlice(RequestHolder requestHolder, QuoteSlice quoteSlice);
	public void completed(RequestHolder requestHolder, ExResultSetMarketSymbolData exResultSetMarketData);
}
