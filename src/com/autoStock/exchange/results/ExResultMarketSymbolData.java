/**
 * 
 */
package com.autoStock.exchange.results;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.trading.platform.ib.core.TickType;
import com.autoStock.trading.platform.ib.definitions.MarketDataDefinitions.TickPriceFields;
import com.autoStock.trading.platform.ib.definitions.MarketDataDefinitions.TickSizeFields;
import com.autoStock.trading.platform.ib.definitions.MarketDataDefinitions.TickTypes;
import com.autoStock.trading.types.MarketSymbolData;

/**
 * @author Kevin Kowalewski
 *
 */
public class ExResultMarketSymbolData {
	public class ExResultSetMarketSymbolData {
		public ArrayList<ExResultRowMarketSymbolData> listOfExResultRowMarketData = new ArrayList<ExResultRowMarketSymbolData>();
		public MarketSymbolData marketSymbolData;
		
		public ExResultSetMarketSymbolData(MarketSymbolData marketSymbolData){
			this.marketSymbolData = marketSymbolData;
		}
	}
	
	public static class ExResultRowMarketSymbolData{
		public TickTypes tickType;
		public TickPriceFields tickPriceField;
		public TickSizeFields tickSizeField;
		public String tickStringValue;
		public double value;
		public Date date;
		
		public ExResultRowMarketSymbolData(TickPriceFields field, double value){
			this.tickType = TickTypes.type_price;
			this.tickPriceField = field;
			this.value = value;
		}
		
		public ExResultRowMarketSymbolData(TickSizeFields field, double value){
			tickType = TickTypes.type_size;
			tickSizeField = field;
			this.value = value;
		}
		
		public ExResultRowMarketSymbolData(int tickType, String value){
			this.tickType = TickTypes.type_string;
			tickStringValue = value;
			if (tickType == TickType.LAST_TIMESTAMP){
				date = new Date(Long.valueOf(value) * 1000);
			}
		}
	}	
}
