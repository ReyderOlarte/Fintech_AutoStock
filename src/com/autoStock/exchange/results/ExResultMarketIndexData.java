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
import com.autoStock.trading.types.MarketIndexData;

/**
 * @author Kevin Kowalewski
 *
 */
public class ExResultMarketIndexData {
	public class ExResultSetMarketIndexData {
		public ArrayList<ExResultRowMarketIndexData> listOfExResultRowMarketIndexData = new ArrayList<ExResultRowMarketIndexData>();
		public MarketIndexData marketIndexData;
		
		public ExResultSetMarketIndexData(MarketIndexData marketIndexData){
			this.marketIndexData = marketIndexData;
		}
	}
	
	public static class ExResultRowMarketIndexData{
		public TickTypes tickType;
		public TickPriceFields tickPriceField;
		public TickSizeFields tickSizeField;
		public String tickStringValue;
		public double value;
		public Date date;
		
		public ExResultRowMarketIndexData(TickPriceFields field, double value){
			this.tickType = TickTypes.type_price;
			this.tickPriceField = field;
			this.value = value;
		}
		
		public ExResultRowMarketIndexData(TickSizeFields field, double value){
			tickType = TickTypes.type_size;
			tickSizeField = field;
			this.value = value;
		}
		
		public ExResultRowMarketIndexData(int tickType, String value){
			this.tickType = TickTypes.type_string;
			tickStringValue = value;
			if (tickType == TickType.LAST_TIMESTAMP){
				date = new Date(Long.valueOf(value) * 1000);
			}
		}
	}	
}
