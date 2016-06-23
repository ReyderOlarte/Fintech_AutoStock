package com.autoStock;

import java.util.ArrayList;

import com.autoStock.algorithm.core.AlgorithmRemodeler;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.algorithm.extras.StrategyOptionsOverride;
import com.autoStock.backtest.BacktestEvaluation;
import com.autoStock.backtest.BacktestEvaluationBuilder;
import com.autoStock.backtest.BacktestEvaluationReader;
import com.autoStock.backtest.SingleBacktest;
import com.autoStock.backtest.BacktestDefinitions.BacktestType;
import com.autoStock.database.DatabaseDefinitions.BasicQueries;
import com.autoStock.database.DatabaseDefinitions.QueryArg;
import com.autoStock.database.DatabaseDefinitions.QueryArgs;
import com.autoStock.database.DatabaseQuery;
import com.autoStock.finance.SecurityTypeHelper.SecurityType;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbWhitelist;
import com.autoStock.internal.Global;
import com.autoStock.r.RJavaController;
import com.autoStock.r.RTestBasic;
import com.autoStock.r.RTestComplete;
import com.autoStock.strategy.StrategyOptions;
import com.autoStock.tools.DateTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;


/**
 * @author Kevin Kowalewski
 *
 */
public class MainTest implements StrategyOptionsOverride {
	
	public MainTest(){
//		Exchange exchange = new Exchange("NYSE");
//		Symbol symbol = new Symbol("MS");
//		
//		HistoricalData historicalData = new HistoricalData(exchange, symbol, DateTools.getDateFromString("09/15/2014"), null, Resolution.min);
//		
//		SingleBacktest singleBacktest = new SingleBacktest(historicalData, AlgorithmMode.mode_backtest_single, 5);
//		new AlgorithmRemodeler(singleBacktest.backtestContainer.algorithm, BacktestEvaluationReader.getPrecomputedModel(exchange, symbol, this)).remodel(true, true, true, false);
//		singleBacktest.selfPopulateBacktestData();
//		singleBacktest.runBacktest();
//		
//		BacktestEvaluation backtestEvaluation = new BacktestEvaluationBuilder().buildEvaluation(singleBacktest.backtestContainer);
//		Co.println(backtestEvaluation.getSingleLine());
//		
//		Co.print(backtestEvaluation.toString());
		
	
		//new RTest().run();
		
		Global.callbackLock.requestLock();
		
		new RTestBasic().run();
	}

	@Override
	public void override(StrategyOptions strategyOptions) {
//		strategyOptions.maxProfitDrawdownPercent.value = -0.10d;
//		strategyOptions.disableAfterLoss.value = -0.25d;
	}

//	public MainTest(){
//		Co.println("--> Main Test!");
//		
//		ArrayList<DbWhitelist> listOfWhitelist = (ArrayList<DbWhitelist>) new DatabaseQuery().getQueryResults(BasicQueries.basic_get_whitelist, new QueryArg(QueryArgs.reason, "manual"));
//		
//		Co.println("--> Have whitelist: " + listOfWhitelist.size());
//		
//		ArrayList<Symbol> listOfSymbols = new ArrayList<Symbol>();
//		
//		for (DbWhitelist dbWhitelist : listOfWhitelist){
//			listOfSymbols.add(new Symbol(dbWhitelist.symbol, SecurityType.type_stock));
//		}
//		
//		new MainBacktest(new Exchange("NYSE"), DateTools.getDateFromString("01/19/2012"), DateTools.getDateFromString("01/19/2012"), listOfSymbols, BacktestType.backtest_adjustment_individual);
//		
//	}
}
