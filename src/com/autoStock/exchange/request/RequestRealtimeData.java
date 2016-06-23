/**
 * 
 */
package com.autoStock.exchange.request;

import com.autoStock.Co;
import com.autoStock.exchange.ExchangeController;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestRealtimeDataListener;
import com.autoStock.exchange.results.ExResultRealtimeData;
import com.autoStock.exchange.results.ExResultRealtimeData.ExResultRowRealtimeData;
import com.autoStock.exchange.results.ExResultRealtimeData.ExResultSetRealtimeData;
import com.autoStock.trading.types.RealtimeData;

/**
 * @author Kevin Kowalewski
 *
 */
public class RequestRealtimeData {
	public RequestHolder requestHolder;
	public RequestRealtimeDataListener requestRealtimeDataListener;
	public ExResultSetRealtimeData exResultSetRealtimeData;
	public RealtimeData typeRealtimeData;

	public RequestRealtimeData(RequestHolder requestHolder, RequestRealtimeDataListener requestRealtimeDataListener, RealtimeData typeRealtimeData) {
		this.requestHolder = requestHolder;
		this.requestHolder.caller = this;
		this.requestRealtimeDataListener = requestRealtimeDataListener;
		this.typeRealtimeData = typeRealtimeData;
		this.exResultSetRealtimeData = new ExResultRealtimeData(). new ExResultSetRealtimeData(typeRealtimeData);
		
		ExchangeController.getIbExchangeInstance().getRealtimeData(typeRealtimeData, requestHolder);
	}
	
	public synchronized void addResult(ExResultRowRealtimeData exResultRowRealtimeData){
		Co.println("addResult");
		this.exResultSetRealtimeData.listOfExResultRowRealtimeData.add(exResultRowRealtimeData);
	}
}
