package com.autoStock.exchange.request;

import java.util.Collections;
import java.util.Date;

import com.autoStock.Co;
import com.autoStock.Log;
import com.autoStock.exchange.ExchangeController;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestHistoricalDataListener;
import com.autoStock.exchange.results.ExResultHistoricalData;
import com.autoStock.exchange.results.ExResultHistoricalData.ExResultRowHistoricalData;
import com.autoStock.exchange.results.ExResultHistoricalData.ExResultSetHistoricalData;
import com.autoStock.tools.ReflectiveComparator;
import com.autoStock.tools.ReflectiveComparator.ListComparator.SortDirection;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Period;
import com.autoStock.trading.types.HistoricalData;

/**
 * @author Kevin Kowalewski
 *
 */
public class RequestHistoricalData {
	public RequestHolder requestHolder;
	public RequestHistoricalDataListener requestHistoricalDataListener;
	public HistoricalData typeHistoricalData;
	public ExResultSetHistoricalData exResultSetHistoricalData;
	
	public RequestHistoricalData(RequestHolder requestHolder, RequestHistoricalDataListener requestListener, HistoricalData typeHistoricalData){
		this.requestHolder = requestHolder;
		this.requestHolder.caller = this;
		this.requestHistoricalDataListener = requestListener;
		this.typeHistoricalData = typeHistoricalData;
		this.exResultSetHistoricalData = new ExResultHistoricalData(). new ExResultSetHistoricalData(typeHistoricalData);
		
//		Co.println("Start / end date: " + this.typeHistoricalData.startDate + "," + this.typeHistoricalData.endDate);
//		Co.println("Sample period: " + this.typeHistoricalData.duration);
//		Co.println("Best res: " + HistoricalDataDefinitions.getBestResolution(this.typeHistoricalData.duration));
		
		if (HistoricalDataDefinitions.getBestResolution(this.typeHistoricalData.duration) != this.typeHistoricalData.resolution){		
			int neededCalls = (int)(this.typeHistoricalData.duration / HistoricalDataDefinitions.getBestPeriod(typeHistoricalData.resolution).seconds) + 1;
			
			Co.println("--> Needed calls: " + neededCalls);
			
			requestHolder.mulitpleRequests = neededCalls;
			
			long neededDuration = this.typeHistoricalData.duration;
			long callStartTime = this.typeHistoricalData.startDate.getTime() / 1000;
			long callEndTime = (this.typeHistoricalData.startDate.getTime() / 1000) + HistoricalDataDefinitions.getBestPeriod(typeHistoricalData.resolution).seconds;
			
			for (int i=0; i<neededCalls; i++){
				if (i == neededCalls-1){
					callEndTime = callStartTime + neededDuration;
				}
								
				HistoricalData tempTypeHistoricalData = typeHistoricalData.copy();
				tempTypeHistoricalData.duration = callEndTime - callStartTime;
				
				if (tempTypeHistoricalData.duration < HistoricalDataDefinitions.MIN_PERIOD){
					Log.w("Period is too short, using entier day instead");
					callEndTime += Period.day.seconds; // (HistoricalDataDefinitions.MIN_PERIOD - tempTypeHistoricalData.duration);
					tempTypeHistoricalData.duration = Period.day.seconds; //HistoricalDataDefinitions.MIN_PERIOD;
				}
								
				tempTypeHistoricalData.startDate = new Date(callStartTime*1000);
				tempTypeHistoricalData.endDate = new Date(callEndTime*1000);
				
				ExchangeController.getIbExchangeInstance().getHistoricalPrice(tempTypeHistoricalData, requestHolder);
				
				neededDuration -= HistoricalDataDefinitions.getBestPeriod(typeHistoricalData.resolution).seconds;
				callStartTime += HistoricalDataDefinitions.getBestPeriod(typeHistoricalData.resolution).seconds + 1;
				callEndTime += HistoricalDataDefinitions.getBestPeriod(typeHistoricalData.resolution).seconds + 1;			
			}
		}else{
			ExchangeController.getIbExchangeInstance().getHistoricalPrice(typeHistoricalData, requestHolder);
		}
	}
	
	public synchronized void addResult(ExResultRowHistoricalData exResultRowHistoricalData){
		this.exResultSetHistoricalData.listOfExResultRowHistoricalData.add(exResultRowHistoricalData);
	}
	
	public synchronized void finished(){
		this.requestHolder.mulitpleRequests--;
		if (this.requestHolder.mulitpleRequests <= 0){
			Collections.sort(exResultSetHistoricalData.listOfExResultRowHistoricalData, new ReflectiveComparator.ListComparator("date", SortDirection.asc));
			this.requestHistoricalDataListener.completed(requestHolder, exResultSetHistoricalData);
		}
	}
}
