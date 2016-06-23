package com.autoStock;

import com.autoStock.exchange.request.MultipleRequestMarketScanner;
import com.autoStock.exchange.request.RequestMarketScanner.MarketScannerType;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.MultipleRequestMarketScannerListener;
import com.autoStock.exchange.results.MultipleResultMarketScanner.MultipleResultRowMarketScanner;
import com.autoStock.exchange.results.MultipleResultMarketScanner.MultipleResultSetMarketScanner;
import com.autoStock.internal.Global;
import com.autoStock.types.Exchange;

/**
 * @author Kevin Kowalewski
 * 
 */
public class MainFilter implements MultipleRequestMarketScannerListener {
	private Exchange exchange;
	private MultipleRequestMarketScanner multipleRequestMarketScanner = new MultipleRequestMarketScanner(this);

	public MainFilter(Exchange exchange) {
		this.exchange = exchange;

		Global.callbackLock.requestLock();

		multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_percent_gain_open);
		// multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_percent_gain);
		// multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_high_open_gap);
		// multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_implied_volatility_gain);
		multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_hot_by_price);
		multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_most_active);
		// multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_top_trade_rate);
		// multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_hot_by_volume);

		multipleRequestMarketScanner.startScanners();
	}

	@Override
	public void failed(RequestHolder requestHolder) {
		Co.println("--> Failed");
	}

	@Override
	public void completed(MultipleResultSetMarketScanner multipleResultSetMarketScanner) {
		for (MultipleResultRowMarketScanner row : multipleResultSetMarketScanner.listOfMultipleResultRowMarketScanner){
			Co.println("--> Symbol, scanner: " + row.symbol + ", " + row.marketScannerType.name());
		}
	}
}
