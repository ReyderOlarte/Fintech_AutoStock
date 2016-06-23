package com.autoStock.exchange.results;

import java.util.ArrayList;

import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */

public class ExResultHistoricalData {
	public class ExResultSetHistoricalData {
		public HistoricalData typeHistoricalData;
		public ArrayList<ExResultRowHistoricalData> listOfExResultRowHistoricalData = new ArrayList<ExResultRowHistoricalData>();
		
		public ExResultSetHistoricalData(HistoricalData typeHistoricalData){
			this.typeHistoricalData = typeHistoricalData;
		}
	}
	
	public static class ExResultRowHistoricalData {
		public final Symbol symbol;
		public final long date;
		public final double priceOpen;
		public final double priceHigh;
		public final double priceLow;
		public final double priceClose; 
		public final int volume;
		public final int count;
		
		public ExResultRowHistoricalData(Symbol symbol, long date, double priceOpen, double priceHigh, double priceLow, double priceClose, int volume, int count) {
			this.symbol = symbol;
			this.date = date * 1000;
			this.priceOpen = priceOpen;
			this.priceHigh = priceHigh;
			this.priceLow = priceLow;
			this.priceClose = priceClose;
			this.volume = volume;
			this.count = count;
		}
	}
}
