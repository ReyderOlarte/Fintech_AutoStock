package com.autoStock.database;

import com.autoStock.database.DatabaseDefinitions.QueryArgs;
import com.autoStock.database.queryResults.QueryResult;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbExchange;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbGson;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbSymbol;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbWhitelist;

/**
 * @author Kevin Kowalewski
 *
 */
public class DatabaseDefinitions {
	
	public static class QueryArg{
		public QueryArgs queryArgs;
		public String value;
		
		public QueryArg(QueryArgs queryArgs, String value) {
			this.queryArgs = queryArgs;
			this.value = value;
		}

		public QueryArg(QueryArgs queryArgs, int value) {
			this.queryArgs = queryArgs;
			this.value = String.valueOf(value);
		}
	}
	
	public enum QueryArgs{
		startDate,
		endDate,
		runDate, 
		dayDate,
		symbol,
		exchange,
		resolution,
		limit,
		reason, 
		balanceInBand, 
		balanceOutBand, 
		percentGainInBand, 
		percentGainOutBand, 
		tradeEntry, 
		tradeReentry, 
		tradeExit, 
		tradeWins, 
		tradeLoss,
		gsonId, 
		gsonString,
	}
	
	
	public static enum BasicQueries {
		basic_historical_price_range("select * from stockHistoricalPrices where symbol = '%s' and exchange = '%s' and resolution = '%s' and dateTime between '%s' and '%s' order by dateTime asc",
			new QueryArgs[]{QueryArgs.symbol, QueryArgs.exchange, QueryArgs.resolution, QueryArgs.startDate, QueryArgs.endDate},
			DbStockHistoricalPrice.class,
			true
		),
		
		basic_single_date_sample_all_stocks("select * from stockHistoricalPrices where dateTime between '%s' and '%s' order by symbol asc",
				new QueryArgs[]{QueryArgs.startDate, QueryArgs.endDate},
				DbStockHistoricalPrice.class,
				true
			),
			
		basic_get_symbol_list_from_exchange("select * from symbols where exchange = '%s' order by rand() limit 100 ",
				new QueryArgs[]{QueryArgs.exchange},
				DbSymbol.class,
				true
			),
			
		basic_get_symbol_list_most_volume("select symbols.symbol, count(symbols.id), sum(sizeVolume) from stockHistoricalPrices left join symbols on symbols.symbol = stockHistoricalPrices.symbol where symbols.exchange = '%s' and (dateTime > '%s' and dateTime < '%s') group by symbols.symbol order by sum(sizeVolume) desc limit %s ",
				new QueryArgs[]{QueryArgs.startDate, QueryArgs.endDate, QueryArgs.exchange, QueryArgs.limit},
				QueryResult.QrSymbolCountFromExchange.class,
				true
			),
			
		basic_get_exchange_info("select * from exchanges where exchange = '%s'", 
				new QueryArgs[]{QueryArgs.exchange},
				DbExchange.class,
				true
			),
			
		basic_get_whitelist("select * from whitelist",
				new QueryArgs[]{QueryArgs.reason},
				DbWhitelist.class,
				false), 
		
		basic_get_backtest_evaluation("select * from gson where id = (select gsonId from backtestResults where symbol='%s' and exchange='%s' order by dateRun desc limit 1);",
				new QueryArgs[]{QueryArgs.symbol, QueryArgs.exchange},
				DbGson.class,
				false),
				
		basic_insert_whitelist("insert into whitelist (symbol, exchange, reason) values('%s','%s','%s')",
				new QueryArgs[]{QueryArgs.symbol, QueryArgs.exchange, QueryArgs.reason},
				null,
				false),
				
		basic_insert_gson("insert into gson (gsonString) values(\"%s\") ",
				new QueryArgs[]{QueryArgs.gsonString},
				null,
				false),
				
		basic_insert_backtest_results("insert into backtestResults(dateStart, dateEnd, dateRun, exchange, symbol, balanceInBand, balanceOutBand, percentGainInBand, percentGainOutBand, tradeEntry, tradeReentry, tradeExit, tradeWins, tradeLoss, gsonId) values ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s', '%s') ",
			new QueryArgs[]{QueryArgs.startDate, QueryArgs.endDate, QueryArgs.runDate, QueryArgs.exchange, QueryArgs.symbol, QueryArgs.balanceInBand, QueryArgs.balanceOutBand, QueryArgs.percentGainInBand, QueryArgs.percentGainOutBand, QueryArgs.tradeEntry, QueryArgs.tradeReentry, QueryArgs.tradeExit, QueryArgs.tradeWins, QueryArgs.tradeLoss, QueryArgs.gsonId},
			null,
			false),
		;
		
		public final String query;
		public final QueryArgs[] listOfFormatterArguments;
		public final Class resultClass;
		public final boolean isCachable; 
		
		BasicQueries(String query, QueryArgs[] listOfFormatterArguments, Class resultClass, boolean cachable){
			this.query = query;
			this.listOfFormatterArguments = listOfFormatterArguments;
			this.resultClass = resultClass;
			this.isCachable = cachable;
		}
	}
}
