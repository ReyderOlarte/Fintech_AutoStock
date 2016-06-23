/**
 * 
 */
package com.autoStock.tools;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.Co;
import com.autoStock.backtest.BacktestUtils;
import com.autoStock.database.DatabaseQuery;
import com.autoStock.database.DatabaseDefinitions.BasicQueries;
import com.autoStock.database.DatabaseDefinitions.QueryArg;
import com.autoStock.database.DatabaseDefinitions.QueryArgs;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin
 *
 */
public class DateConditions {	
	public static abstract class BaseDateCondition {
		public Date date;
		public abstract boolean isValid();
		
		public void setDate(Date date){
			this.date = date;
		}
	}
	
	public static class QuoteAvailableDateCondition extends BaseDateCondition {
		public Symbol symbol;
		public Exchange exchange;
		
		public QuoteAvailableDateCondition(Exchange exchange, Symbol symbol, Date date) {
			this.symbol = symbol;
			this.exchange = exchange;
			this.date = date;
		}
		
		public QuoteAvailableDateCondition(HistoricalData historicalData) {
			this.exchange = historicalData.exchange;
			this.symbol = historicalData.symbol;
		}

		@Override
		public boolean isValid() {
			HistoricalData historicalData = new HistoricalData(exchange, symbol, (Date)date.clone(), (Date)date.clone(), Resolution.min);
			historicalData.setStartAndEndDatesToExchange();
			ArrayList<DbStockHistoricalPrice> listOfResults = (ArrayList<DbStockHistoricalPrice>) new DatabaseQuery().getQueryResults(BasicQueries.basic_historical_price_range, new QueryArg(QueryArgs.symbol, historicalData.symbol.name), new QueryArg(QueryArgs.exchange, historicalData.exchange.name), new QueryArg(QueryArgs.resolution, historicalData.resolution.asMinutes()), new QueryArg(QueryArgs.startDate, DateTools.getSqlDate(historicalData.startDate)), new QueryArg(QueryArgs.endDate, DateTools.getSqlDate(historicalData.endDate)));
			
			return listOfResults.size() > 0;
		}
	}
}
