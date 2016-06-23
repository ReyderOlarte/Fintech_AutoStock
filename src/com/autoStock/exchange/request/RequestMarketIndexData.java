/**
 * 
 */
package com.autoStock.exchange.request;

import java.util.Date;

import com.autoStock.exchange.ExchangeController;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestMarketIndexDataListener;
import com.autoStock.exchange.results.ExResultMarketIndexData;
import com.autoStock.exchange.results.ExResultMarketIndexData.ExResultRowMarketIndexData;
import com.autoStock.exchange.results.ExResultMarketIndexData.ExResultSetMarketIndexData;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.IndexSliceTools;
import com.autoStock.trading.platform.ib.definitions.MarketDataDefinitions.TickTypes;
import com.autoStock.trading.types.MarketIndexData;
import com.autoStock.types.IndexSlice;

/**
 * @author Kevin Kowalewski
 * 
 */
public class RequestMarketIndexData {
	public RequestHolder requestHolder;
	public RequestMarketIndexDataListener requestMarketIndexDataListener;
	public ExResultSetMarketIndexData exResultSetMarketIndexData;
	public MarketIndexData marketIndexData;
	private Thread threadForSliceCollector;
	private int sliceMilliseconds;
	private long receivedTimestamp = 0;
	private IndexSlice indexSlicePrevious = new IndexSlice();
	private boolean isCancelled = false;

	public RequestMarketIndexData(RequestHolder requestHolder, RequestMarketIndexDataListener requestMarketIndexDataListener, MarketIndexData marketIndexData, int sliceMilliseconds) {
		this.requestHolder = requestHolder;
		this.requestHolder.caller = this;
		this.requestMarketIndexDataListener = requestMarketIndexDataListener;
		this.marketIndexData = marketIndexData;
		this.exResultSetMarketIndexData = new ExResultMarketIndexData(). new ExResultSetMarketIndexData(marketIndexData);
		this.sliceMilliseconds = sliceMilliseconds;
		
		ExchangeController.getIbExchangeInstance().getMarketDataForIndex(marketIndexData.exchange, marketIndexData.index, requestHolder);
	}
	
	public synchronized void addResult(ExResultRowMarketIndexData exResultRowMarketIndexData){
		if (exResultRowMarketIndexData.tickType == TickTypes.type_string){
			if (sliceMilliseconds != 0 && receivedTimestamp == 0){
				receivedTimestamp = Long.valueOf(exResultRowMarketIndexData.tickStringValue);
				runThreadForSliceCollector(sliceMilliseconds);
			}
		}
		exResultSetMarketIndexData.listOfExResultRowMarketIndexData.add(exResultRowMarketIndexData);
	}
	
	public void runThreadForSliceCollector(final int sliceMilliseconds){
		Date date = new Date(receivedTimestamp*1000);
		
		threadForSliceCollector = new Thread(new Runnable(){
			@Override
			public void run() {
				while (true){
					try {Thread.sleep(sliceMilliseconds);}catch(InterruptedException e){return;}
					
					synchronized(RequestMarketIndexData.this){
						IndexSlice indexSlice = new IndexSliceTools().getIndexSlice(exResultSetMarketIndexData.listOfExResultRowMarketIndexData, marketIndexData.index);
						new IndexSliceTools().mergeIndexSlices(indexSlicePrevious, indexSlice);

						indexSlice.dateTime = DateTools.getForeignDateFromLocalTime(DateTools.getTimeFromDate(new Date()), marketIndexData.exchange.timeZone); 
						indexSlicePrevious = indexSlice;
						
						exResultSetMarketIndexData.listOfExResultRowMarketIndexData.clear();

						requestMarketIndexDataListener.receiveIndexSlice(requestHolder, indexSlice);
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
