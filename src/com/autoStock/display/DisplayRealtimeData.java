/**
 * 
 */
package com.autoStock.display;

import com.autoStock.exchange.request.RequestRealtimeData;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestRealtimeDataListener;
import com.autoStock.exchange.results.ExResultRealtimeData.ExResultSetRealtimeData;
import com.autoStock.trading.types.RealtimeData;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 *
 */
public class DisplayRealtimeData {

	public RealtimeData typeRealtimeData;
	
	public DisplayRealtimeData(RealtimeData typeRealtimeData) {
		this.typeRealtimeData = typeRealtimeData;
	}
	
	public void display(){
		new RequestRealtimeData(new RequestHolder(null), new RequestRealtimeDataListener() {
			@Override
			public void failed(RequestHolder requestHolder) {}
			
			@Override
			public void completed(RequestHolder requestHolder, ExResultSetRealtimeData exResultSetRealtimeData) {}

			@Override
			public void receiveQuoteSlice(RequestHolder requestHolder, QuoteSlice typeQuoteSlice) {
				
			}
		}, typeRealtimeData);
	}
}
