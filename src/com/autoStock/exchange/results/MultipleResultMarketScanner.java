package com.autoStock.exchange.results;

import java.util.ArrayList;

import com.autoStock.exchange.request.RequestMarketScanner.MarketScannerType;

/**
 * @author Kevin Kowalewski
 *
 */
public class MultipleResultMarketScanner {
	public static class MultipleResultSetMarketScanner {
		public ArrayList<MultipleResultRowMarketScanner> listOfMultipleResultRowMarketScanner = new ArrayList<MultipleResultRowMarketScanner>();
	}
	
	public static class MultipleResultRowMarketScanner{
		public String symbol;
		public MarketScannerType marketScannerType;
		
		public MultipleResultRowMarketScanner(String symbol, MarketScannerType marketScannerType) {
			this.symbol = symbol;
			this.marketScannerType = marketScannerType;
		}
	}
}
