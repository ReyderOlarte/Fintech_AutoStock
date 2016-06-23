package com.autoStock.exchange.request;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.autoStock.exchange.request.RequestMarketScanner.MarketScannerType;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.MultipleRequestMarketScannerListener;
import com.autoStock.exchange.request.listener.RequestMarketScannerListener;
import com.autoStock.exchange.results.ExResultMarketScanner.ExResultRowMarketScanner;
import com.autoStock.exchange.results.ExResultMarketScanner.ExResultSetMarketScanner;
import com.autoStock.exchange.results.MultipleResultMarketScanner;
import com.autoStock.exchange.results.MultipleResultMarketScanner.MultipleResultRowMarketScanner;
import com.autoStock.exchange.results.MultipleResultMarketScanner.MultipleResultSetMarketScanner;
import com.autoStock.types.Exchange;
import com.google.gson.internal.Pair;

/**
 * @author Kevin Kowalewski
 *
 */
public class MultipleRequestMarketScanner implements RequestMarketScannerListener {
	private ArrayList<Pair<MarketScannerType, Exchange>> listOfPair = new ArrayList<Pair<MarketScannerType, Exchange>>();
	private ArrayList<RequestMarketScanner> listOfRequestmarketScanner = new ArrayList<RequestMarketScanner>();
	private AtomicInteger atomicIntegerForScannerCount = new AtomicInteger();
	private MultipleRequestMarketScannerListener multipleRequestMarketScannerListener;
	private MultipleResultMarketScanner.MultipleResultSetMarketScanner multipleResultSetMarketScanner = new MultipleResultSetMarketScanner();
	
	public MultipleRequestMarketScanner(MultipleRequestMarketScannerListener listener){
		multipleRequestMarketScannerListener = listener;
	}
	
	public synchronized void addRequest(Exchange exchange, MarketScannerType marketScannerType){
		listOfPair.add(new Pair<MarketScannerType, Exchange>(marketScannerType, exchange));
	}
	
	public void startScanners(){
		atomicIntegerForScannerCount.set(listOfPair.size());
		
		for (Pair<MarketScannerType, Exchange> pair : listOfPair){
			RequestMarketScanner requestMarketScanner = new RequestMarketScanner(new RequestHolder(this), pair.second, pair.first);
			listOfRequestmarketScanner.add(requestMarketScanner);
		}
	}

	@Override
	public synchronized void failed(RequestHolder requestHolder) {
		
	}

	@Override
	public synchronized void completed(RequestHolder requestHolder, ExResultSetMarketScanner exResultSetMarketScanner, MarketScannerType marketScannerType) {
//		Co.println("--> Received: " + atomicIntegerForScannerCount.get() + "," + exResultSetMarketScanner.listOfExResultRowMarketScanner.size());
		for (ExResultRowMarketScanner exResultRowMarketScanner : exResultSetMarketScanner.listOfExResultRowMarketScanner){
			if (containsSymbol(exResultRowMarketScanner.symbol) == false){
				multipleResultSetMarketScanner.listOfMultipleResultRowMarketScanner.add(new MultipleResultRowMarketScanner(exResultRowMarketScanner.symbol, marketScannerType));
			}
		}
		
		if (atomicIntegerForScannerCount.decrementAndGet() == 0){
			atomicIntegerForScannerCount.set(listOfPair.size());
			multipleRequestMarketScannerListener.completed(multipleResultSetMarketScanner);
			clearResults();
		}
	}
	
	private void clearResults(){
		for (RequestMarketScanner requestMarketScanner : listOfRequestmarketScanner){
			requestMarketScanner.clearResults();
		}
		
		multipleResultSetMarketScanner.listOfMultipleResultRowMarketScanner.clear();
	}

	public void stopScanner() {
		for (RequestMarketScanner requestMarketScanner : listOfRequestmarketScanner){
			requestMarketScanner.cancel();
		}
		
		listOfRequestmarketScanner.clear();
	}
	
	private boolean containsSymbol(String symbol){
		for (MultipleResultRowMarketScanner result : multipleResultSetMarketScanner.listOfMultipleResultRowMarketScanner){
			if (result.symbol.equals(symbol)){
				return true;
			}
		}
		
		return false;
	}
}
