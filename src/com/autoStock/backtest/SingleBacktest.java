package com.autoStock.backtest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.algorithm.core.AlgorithmRemodeler;
import com.autoStock.backtest.BacktestUtils.LookDirection;
import com.autoStock.database.DatabaseDefinitions.BasicQueries;
import com.autoStock.database.DatabaseDefinitions.QueryArg;
import com.autoStock.database.DatabaseDefinitions.QueryArgs;
import com.autoStock.database.DatabaseQuery;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.tools.Benchmark;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.DateConditions.QuoteAvailableDateCondition;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.trading.types.HistoricalDataList;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class SingleBacktest implements ListenerOfBacktest {
	public BacktestContainer backtestContainer;
	private HistoricalData historicalData;
	private ArrayList<HistoricalData> listOfHistoricalData = new ArrayList<HistoricalData>();
	public int currentBacktestDayIndex = 0;
	private ListenerOfBacktest listenerOfBacktestCompleted;
	
	public SingleBacktest(HistoricalData historicalData, AlgorithmMode algorithmMode){
		this.historicalData = historicalData;
		listOfHistoricalData = BacktestUtils.getHistoricalDataListForDates(historicalData);
		backtestContainer = new BacktestContainer(historicalData.symbol, historicalData.exchange, this, algorithmMode);
	}
	
	public SingleBacktest(HistoricalData historicalData, AlgorithmMode algorithmMode, int days){
		this.historicalData = historicalData;
		listOfHistoricalData = BacktestUtils.getHistoricalDataListForDates(new HistoricalData(historicalData.exchange, historicalData.symbol, historicalData.startDate, null, historicalData.resolution), LookDirection.forward, days);
		backtestContainer = new BacktestContainer(historicalData.symbol, historicalData.exchange, this, algorithmMode);
	}

	@Override
	public void onCompleted(Symbol symbol, AlgorithmBase algorithmBase) {
//		Co.println("--> Backtest completed... " + symbol.symbolName + ", " + currentBacktestDayIndex + ", " + backtestContainer.isIncomplete());
		
		currentBacktestDayIndex++;
		
		if (currentBacktestDayIndex == listOfHistoricalData.size()) {
			backtestContainer.markAsComplete();
		}else{
			selfPopulateBacktestData();
			runBacktest();
			return;
		}
		
		if (currentBacktestDayIndex == listOfHistoricalData.size()){
			if (listenerOfBacktestCompleted != null){listenerOfBacktestCompleted.onCompleted(symbol, algorithmBase);}
		}
	}
	
	public void selfPopulateBacktestData(){
		HistoricalData historicalData = listOfHistoricalData.get(currentBacktestDayIndex);
		ArrayList<DbStockHistoricalPrice> listOfResults = (ArrayList<DbStockHistoricalPrice>) new DatabaseQuery().getQueryResults(BasicQueries.basic_historical_price_range, new QueryArg(QueryArgs.symbol, historicalData.symbol.name), new QueryArg(QueryArgs.exchange, historicalData.exchange.name), new QueryArg(QueryArgs.resolution, historicalData.resolution.asMinutes()), new QueryArg(QueryArgs.startDate, DateTools.getSqlDate(historicalData.startDate)), new QueryArg(QueryArgs.endDate, DateTools.getSqlDate(historicalData.endDate)));
		
		if (listOfResults.size() == 0){
			Co.println("--> Warning! No backtest data for symbol: " + historicalData.symbol.name + " on " + historicalData.startDate + " to " + historicalData.endDate);
		}else{
//			Co.println("--> Size: " + listOfResults.size());
		}
		
		backtestContainer.setBacktestData(listOfResults, historicalData);
	}
	
	public void setBacktestData(ArrayList<DbStockHistoricalPrice> listOfDbStockHistoricalPrice){
		if (listOfDbStockHistoricalPrice.size() == 0){
			throw new IllegalStateException("Backtest data size cannot be zero");
		}
		backtestContainer.setBacktestData(listOfDbStockHistoricalPrice, historicalData);
	}
	
	public void setListenerOfBacktestCompleted(ListenerOfBacktest listener){
		this.listenerOfBacktestCompleted = listener;
	}

	public void runBacktest() {
		backtestContainer.prepare();
		backtestContainer.perform(true);
	}

	public void remodel(AlgorithmModel algorithmModel) {
		new AlgorithmRemodeler(backtestContainer.algorithm, algorithmModel).remodel();
	}
}
