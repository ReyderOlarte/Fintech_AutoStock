/**
 * 
 */
package com.autoStock.exchange.request;

import com.autoStock.exchange.ExchangeController;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestMarketScannerListener;
import com.autoStock.exchange.results.ExResultMarketScanner;
import com.autoStock.exchange.results.ExResultMarketScanner.ExResultRowMarketScanner;
import com.autoStock.exchange.results.ExResultMarketScanner.ExResultSetMarketScanner;
import com.autoStock.types.Exchange;

/**
 * @author Kevin Kowalewski
 *
 */
public class RequestMarketScanner {
	private ExResultSetMarketScanner exResultSetMarketScanner;
	private RequestHolder requestHolder;
	private Exchange exchange;
	private MarketScannerType marketScannerType;
	
	public enum MarketScannerType{
		type_percent_gain_open,
		type_percent_loss_open,
		type_percent_gain,
		type_high_open_gap,
		type_implied_volatility_gain,
		type_most_active,
		type_top_trade_rate,
		type_top_volume_rate,
		type_high_volume,
		type_hot_by_price,
		type_hot_by_volume,
		none,
	}
	
	public RequestMarketScanner(RequestHolder requestHolder, Exchange exchange, MarketScannerType marketScannerType){
		this.requestHolder = requestHolder;
		this.requestHolder.caller = this;
		this.exchange = exchange;
		this.exResultSetMarketScanner = new ExResultMarketScanner(). new ExResultSetMarketScanner();
		this.marketScannerType = marketScannerType;
		
		ExchangeController.getIbExchangeInstance().getScanner(requestHolder, exchange, marketScannerType);
	}
	
	public synchronized void addResult(ExResultRowMarketScanner exResultRowMarketScanner){
		this.exResultSetMarketScanner.listOfExResultRowMarketScanner.add(exResultRowMarketScanner);
	}
	
	public synchronized void finished(){
		((RequestMarketScannerListener)requestHolder.callback).completed(requestHolder, exResultSetMarketScanner, marketScannerType);
//		Co.println("Finished market scanner. Result size:" + exResultSetMarketScanner.listOfExResultRowMarketScanner.size());
	}

	public void clearResults() {
		exResultSetMarketScanner.listOfExResultRowMarketScanner.clear();
	}
	
	public void cancel(){
		ExchangeController.getIbExchangeInstance().cancelScanner(requestHolder);
	}
}
