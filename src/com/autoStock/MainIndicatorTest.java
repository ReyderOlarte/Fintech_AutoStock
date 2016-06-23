package com.autoStock;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.database.DatabaseDefinitions.BasicQueries;
import com.autoStock.database.DatabaseDefinitions.QueryArg;
import com.autoStock.database.DatabaseDefinitions.QueryArgs;
import com.autoStock.database.DatabaseQuery;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.internal.Global;
import com.autoStock.tools.DateTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class MainIndicatorTest {
	private ArrayList<DbStockHistoricalPrice> listOfResults;
	public MainIndicatorTest(Date startDate, Date endDate, Exchange exchange, Symbol symbol){
		Global.callbackLock.requestLock();
		
		HistoricalData historicalData = new HistoricalData(exchange, null, startDate, endDate, Resolution.min);

		historicalData.startDate.setHours(exchange.timeOpenForeign.hours);
		historicalData.startDate.setMinutes(exchange.timeOpenForeign.minutes);
		historicalData.endDate.setHours(exchange.timeCloseForeign.hours);
		historicalData.endDate.setMinutes(exchange.timeCloseForeign.minutes);
		historicalData.symbol = symbol;
		
		listOfResults = (ArrayList<DbStockHistoricalPrice>) new DatabaseQuery().getQueryResults(BasicQueries.basic_historical_price_range, new QueryArg(QueryArgs.symbol, historicalData.symbol.name), new QueryArg(QueryArgs.exchange, historicalData.exchange.name), new QueryArg(QueryArgs.resolution, historicalData.resolution.asMinutes()), new QueryArg(QueryArgs.startDate, DateTools.getSqlDate(historicalData.startDate)), new QueryArg(QueryArgs.endDate, DateTools.getSqlDate(historicalData.endDate)));
		Co.println("--> Have quotes: " + listOfResults.size());
	}
	
	private void execute(){
		
	}
}
