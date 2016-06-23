package com.autoStock.backtest;

import java.text.DecimalFormat;
import java.util.Date;

import com.autoStock.database.DatabaseDefinitions.BasicQueries;
import com.autoStock.database.DatabaseDefinitions.QueryArg;
import com.autoStock.database.DatabaseDefinitions.QueryArgs;
import com.autoStock.database.DatabaseQuery;
import com.autoStock.internal.GsonProvider;
import com.autoStock.tools.DateTools;

/**
 * @author Kevin Kowalewski
 *
 */
public class BacktestEvaluationWriter {
	public void writeToDatabase(BacktestEvaluation backtestEvaluation, boolean includeOutOfSample){

		BacktestEvaluation outOfSampleEvaluation = null;
		
		if (includeOutOfSample){
			 outOfSampleEvaluation = new BacktestEvaluationBuilder().buildOutOfSampleEvaluation(backtestEvaluation);
		}
		
		int gsonId = new DatabaseQuery().insert(BasicQueries.basic_insert_gson, new QueryArg(QueryArgs.gsonString, new GsonProvider().getGsonForBacktestEvaluations().toJson(backtestEvaluation).replace("\"", "\\\"")));
		
		new DatabaseQuery().insert(BasicQueries.basic_insert_backtest_results,
			new QueryArg(QueryArgs.startDate, DateTools.getSqlDate(backtestEvaluation.dateStart)),
			new QueryArg(QueryArgs.endDate, DateTools.getSqlDate(backtestEvaluation.dateEnd)),
			new QueryArg(QueryArgs.runDate, DateTools.getSqlDate(new Date())),
			new QueryArg(QueryArgs.exchange, backtestEvaluation.exchange.name),
			new QueryArg(QueryArgs.symbol, backtestEvaluation.symbol.name),
			new QueryArg(QueryArgs.balanceInBand, new DecimalFormat("#.00").format(backtestEvaluation.accountBalance)),
			new QueryArg(QueryArgs.balanceOutBand, new DecimalFormat("#.00").format(outOfSampleEvaluation == null ? 0 : outOfSampleEvaluation.accountBalance)),
			new QueryArg(QueryArgs.percentGainInBand, new DecimalFormat("#.00").format(backtestEvaluation.percentYield)),
			new QueryArg(QueryArgs.percentGainOutBand, "0"),
			new QueryArg(QueryArgs.tradeEntry, String.valueOf(backtestEvaluation.transactionDetails.countForTradeLongEntry + backtestEvaluation.transactionDetails.countForTradeShortEntry)),
			new QueryArg(QueryArgs.tradeReentry, String.valueOf(backtestEvaluation.transactionDetails.countForTradesReentry)),
			new QueryArg(QueryArgs.tradeExit, String.valueOf(backtestEvaluation.transactionDetails.countForTradeExit)),
			new QueryArg(QueryArgs.tradeWins, String.valueOf(backtestEvaluation.transactionDetails.countForTradesProfit)),
			new QueryArg(QueryArgs.tradeLoss, String.valueOf(backtestEvaluation.transactionDetails.countForTradesLoss)),
			new QueryArg(QueryArgs.gsonId, String.valueOf(gsonId))
		);
	}
}
