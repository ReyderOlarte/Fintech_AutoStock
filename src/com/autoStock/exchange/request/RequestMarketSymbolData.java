/**
 * 
 */
package com.autoStock.exchange.request;

import java.util.Date;

import com.autoStock.exchange.ExchangeController;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestMarketSymbolDataListener;
import com.autoStock.exchange.results.ExResultMarketSymbolData;
import com.autoStock.exchange.results.ExResultMarketSymbolData.ExResultRowMarketSymbolData;
import com.autoStock.exchange.results.ExResultMarketSymbolData.ExResultSetMarketSymbolData;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.QuoteSliceTools;
import com.autoStock.trading.platform.ib.definitions.MarketDataDefinitions.TickTypes;
import com.autoStock.trading.types.MarketSymbolData;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 * 
 */
public class RequestMarketSymbolData {
	public RequestHolder requestHolder;
	public RequestMarketSymbolDataListener requestMarketSymbolDataListener;
	public ExResultSetMarketSymbolData exResultSetMarketSymbolData;
	public MarketSymbolData marketSymbolData;
	private Thread threadForSliceCollector;
	private int sliceMilliseconds;
	private long receivedTimestamp = 0;
	private QuoteSlice quoteSlicePrevious = new QuoteSlice();
	private boolean isCancelled = false;

	public RequestMarketSymbolData(RequestHolder requestHolder, RequestMarketSymbolDataListener requestMarketDataListener, MarketSymbolData marketSymbolData, int sliceMilliseconds) {
		this.requestHolder = requestHolder;
		this.requestHolder.caller = this;
		this.requestMarketSymbolDataListener = requestMarketDataListener;
		this.marketSymbolData = marketSymbolData;
		this.exResultSetMarketSymbolData = new ExResultMarketSymbolData(). new ExResultSetMarketSymbolData(marketSymbolData);
		this.sliceMilliseconds = sliceMilliseconds;
		
		ExchangeController.getIbExchangeInstance().getMarketDataForSymbol(marketSymbolData.exchange, marketSymbolData.symbol, requestHolder);
	}
	
	public synchronized void addResult(ExResultRowMarketSymbolData exResultRowMarketData){
		if (exResultRowMarketData.tickType == TickTypes.type_string){
			if (sliceMilliseconds != 0 && receivedTimestamp == 0){
				receivedTimestamp = Long.valueOf(exResultRowMarketData.tickStringValue);
				runThreadForSliceCollector(sliceMilliseconds);
			}
		}
		exResultSetMarketSymbolData.listOfExResultRowMarketData.add(exResultRowMarketData);
	}
	
	public void runThreadForSliceCollector(final int sliceMilliseconds){
		Date date = new Date(receivedTimestamp*1000);
		
		threadForSliceCollector = new Thread(new Runnable(){
			@Override
			public void run() {
				while (true){
					try {Thread.sleep(sliceMilliseconds);}catch(InterruptedException e){return;}
					
					synchronized(RequestMarketSymbolData.this){
						QuoteSlice quoteSlice = new QuoteSliceTools().getQuoteSlice(exResultSetMarketSymbolData.listOfExResultRowMarketData, marketSymbolData.symbol);
						new QuoteSliceTools().mergeQuoteSlices(quoteSlicePrevious, quoteSlice);
						quoteSlice.dateTime = DateTools.getForeignDateFromLocalTime(DateTools.getTimeFromDate(new Date()), marketSymbolData.exchange.timeZone);
						
						quoteSlicePrevious = quoteSlice;
						
						exResultSetMarketSymbolData.listOfExResultRowMarketData.clear();
//						Co.println("O,H,L,C,V: " + quoteSlice.priceOpen + ", " + quoteSlice.priceHigh + ", " + quoteSlice.priceLow + ", " + quoteSlice.priceClose + ", " + quoteSlice.sizeVolume);
						requestMarketSymbolDataListener.receiveQuoteSlice(requestHolder, quoteSlice);
					}
				}
			}
		});
		
		this.threadForSliceCollector.start();
	}
	
	public void cancel(){
		if (threadForSliceCollector != null && isCancelled == false){
			threadForSliceCollector.interrupt();
			ExchangeController.getIbExchangeInstance().cancelMarketData(requestHolder);
			isCancelled = true;
		}
	}
}
