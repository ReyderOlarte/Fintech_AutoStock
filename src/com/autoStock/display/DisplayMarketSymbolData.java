/**
 * 
 */
package com.autoStock.display;

import com.autoStock.Co;
import com.autoStock.exchange.request.RequestMarketSymbolData;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestMarketSymbolDataListener;
import com.autoStock.exchange.results.ExResultMarketSymbolData.ExResultSetMarketSymbolData;
import com.autoStock.internal.Global;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Period;
import com.autoStock.trading.types.MarketSymbolData;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 *
 */
public class DisplayMarketSymbolData {
	private MarketSymbolData marketData;
	
	public DisplayMarketSymbolData(MarketSymbolData marketData){
		this.marketData = marketData;
		Global.callbackLock.requestLock();
	}
	
	public void display(){
		new RequestMarketSymbolData(new RequestHolder(null), new RequestMarketSymbolDataListener() {
			@Override
			public void failed(RequestHolder requestHolder) {
				
			}
			
			@Override
			public void completed(RequestHolder requestHolder, ExResultSetMarketSymbolData exResultSetMarketData) {
				Co.println("Completed!");
				
			}

			@Override
			public void receiveQuoteSlice(RequestHolder requestHolder, QuoteSlice quoteSlice) {
				Co.println("--> Quote: " + quoteSlice.priceOpen + ", " + quoteSlice.priceHigh + ", " + quoteSlice.priceLow + ", " + quoteSlice.priceClose + ", " + quoteSlice.sizeVolume);
				
			}
		}, marketData, Period.min.seconds * 1000);
	}
}
