package com.autoStock.index;

import com.autoStock.Co;
import com.autoStock.exchange.request.RequestMarketIndexData;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestMarketIndexDataListener;
import com.autoStock.exchange.results.ExResultMarketIndexData.ExResultSetMarketIndexData;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Period;
import com.autoStock.trading.types.MarketIndexData;
import com.autoStock.types.Exchange;
import com.autoStock.types.Index;
import com.autoStock.types.IndexSlice;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndexMarketDataProvider {
	private RequestMarketIndexData requestMarketIndexData;
	
	public IndexMarketDataProvider(Exchange exchange, Index index){
		requestMarketIndexData = new RequestMarketIndexData(new RequestHolder(this), new RequestMarketIndexDataListener() {
			@Override
			public void failed(RequestHolder requestHolder) {
				Co.println("--> Failed...");
			}
			
			@Override
			public void receiveIndexSlice(RequestHolder requestHolder, IndexSlice indexSlice) {
				Co.println("--> Received index slice: " + indexSlice.valueClose);
			}
			
			@Override
			public void completed(RequestHolder requestHolder, ExResultSetMarketIndexData exResultSetIndexData) {
				Co.println("--> Completed...");
			}
		}, new MarketIndexData(exchange, index), Period.min.seconds * 1000);	
	}
	
	public IndexSlice getIndexSlice(){
		return new IndexSlice();
	}
	
	public void cancel(){
		requestMarketIndexData.cancel();
	}
}
