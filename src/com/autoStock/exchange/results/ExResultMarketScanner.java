/**
 * 
 */
package com.autoStock.exchange.results;

import java.util.ArrayList;

/**
 * @author Kevin Kowalewski
 *
 */
public class ExResultMarketScanner {
	public class ExResultSetMarketScanner {
		public ArrayList<ExResultRowMarketScanner> listOfExResultRowMarketScanner = new ArrayList<ExResultRowMarketScanner>();
	}
	
	public static class ExResultRowMarketScanner{
		public String symbol;
		public int rank;
		
		public ExResultRowMarketScanner(String symbol, int rank) {
			this.symbol = symbol;
			this.rank = rank;
		}
	}
}
