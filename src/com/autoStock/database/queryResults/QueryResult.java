/**
 * 
 */
package com.autoStock.database.queryResults;

import com.autoStock.types.basic.Time;

/**
 * @author Kevin Kowalewski
 *
 */
public class QueryResult {
	 public static class QrSymbolCountFromExchange{
		 public int count;
		 public long sizeVolume;
		 public String symbol;
		 
		 public QrSymbolCountFromExchange(String symbol, int count, long sizeVolume) {
			 this.count = count;
			 this.symbol = symbol;
			 this.sizeVolume = sizeVolume;
		 }
	 }
	 
	 public static class QrExchange{
		 public String exchange;
		 public Time timeOpen;
		 public Time timeClose;
		 public Time timeOffset;
		 public String currency;
	 }
}
