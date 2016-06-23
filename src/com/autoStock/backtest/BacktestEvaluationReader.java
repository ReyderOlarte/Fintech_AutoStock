/**
 * 
 */
package com.autoStock.backtest;

import java.util.ArrayList;

import com.autoStock.Co;
import com.autoStock.algorithm.extras.StrategyOptionsOverride;
import com.autoStock.database.DatabaseDefinitions.BasicQueries;
import com.autoStock.database.DatabaseDefinitions.QueryArg;
import com.autoStock.database.DatabaseDefinitions.QueryArgs;
import com.autoStock.database.DatabaseQuery;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbGson;
import com.autoStock.internal.GsonProvider;
import com.autoStock.strategy.StrategyOptionDefaults;
import com.autoStock.strategy.StrategyOptions;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin
 *
 */
public class BacktestEvaluationReader {	
	public static BacktestEvaluation getPrecomputedEvaluation(Exchange exchange, Symbol symbol){
		ArrayList<DbGson> listOfGsonResults = (ArrayList<DbGson>) new DatabaseQuery().getQueryResults(BasicQueries.basic_get_backtest_evaluation, new QueryArg(QueryArgs.symbol, symbol.name), new QueryArg(QueryArgs.exchange, exchange.name));
		
		if (listOfGsonResults.size() > 0){
			BacktestEvaluation backtestEvaluation = new GsonProvider().getGsonForBacktestEvaluations().fromJson(listOfGsonResults.get(0).gsonString, BacktestEvaluation.class);				
			return backtestEvaluation;
		}
		return null;
	}
	
	public static AlgorithmModel getPrecomputedModel(Exchange exchange, Symbol symbol){
		return getPrecomputedModel(exchange, symbol, null);
	}
	
	
	/**
	 * Be careful with this method as the override modifies in place and not a clone of the AlgorithmModel StrategyOptions
	 * @param exchange
	 * @param symbol
	 * @param override
	 * @return
	 */
	public static AlgorithmModel getPrecomputedModel(Exchange exchange, Symbol symbol, StrategyOptionsOverride override){
		BacktestEvaluation backtestEvaluation = getPrecomputedEvaluation(exchange, symbol);
		
		if (backtestEvaluation == null){
			AlgorithmModel algorithmModel = new AlgorithmModel();
			algorithmModel.strategyOptions = new StrategyOptionDefaults().getDefaultStrategyOptions();
			return algorithmModel;
		}
		
		if (override != null){
			//Co.println("--> Warning: Using SOOverride");
			override.override(backtestEvaluation.algorithmModel.strategyOptions);
		}
		
		return backtestEvaluation.algorithmModel;
	}
}
