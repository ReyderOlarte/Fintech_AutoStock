/**
 * 
 */
package com.autoStock.exchange.results;

import java.util.ArrayList;

import com.autoStock.trading.platform.ib.definitions.MarketDataDefinitions.TickPriceFields;
import com.autoStock.trading.types.RealtimeData;

/**
 * @author Kevin Kowalewski
 *
 */
public class ExResultRealtimeData {
	public class ExResultSetRealtimeData {
		public RealtimeData typeRealtimeData;
		public ArrayList<ExResultRowRealtimeData> listOfExResultRowRealtimeData = new ArrayList<ExResultRowRealtimeData>();
		
		public ExResultSetRealtimeData(RealtimeData typeRealtimeData){
			this.typeRealtimeData = typeRealtimeData;
		}
	}
	
	public static class ExResultRowRealtimeData{
		TickPriceFields field;
		String value;
		
		public ExResultRowRealtimeData(TickPriceFields field, String value){
			this.field = field;
			this.value = value;
		}
	}	
}
