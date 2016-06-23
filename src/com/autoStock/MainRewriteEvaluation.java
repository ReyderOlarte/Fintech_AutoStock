/**
 * 
 */
package com.autoStock;

import com.autoStock.backtest.BacktestEvaluation;
import com.autoStock.backtest.BacktestEvaluationReader;
import com.autoStock.backtest.BacktestEvaluationWriter;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForDI;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForMACD;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForSTORSI;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForUO;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.TacticResolver.SignalPointTactic;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin
 *
 */
public class MainRewriteEvaluation {
	public void run(){
		Exchange exchange = new Exchange("NYSE");
		Symbol symbol = new Symbol("MS");
		
		BacktestEvaluation backtestEvaluation = BacktestEvaluationReader.getPrecomputedEvaluation(exchange, symbol);
		
		//backtestEvaluation.algorithmModel.strategyOptions.listOfSignalMetricType.clear();
		//backtestEvaluation.algorithmModel.strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_encog);
		//backtestEvaluation.algorithmModel.strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_crossover);
		
//		backtestEvaluation.algorithmModel.strategyOptions.signalPointTacticForEntry.value = SignalPointTactic.tactic_combined;
//		backtestEvaluation.algorithmModel.strategyOptions.signalPointTacticForExit.value = SignalPointTactic.tactic_combined;
		
		backtestEvaluation.algorithmModel.strategyOptions.maxProfitDrawdownPercent.value = -0.30;
		
		new BacktestEvaluationWriter().writeToDatabase(backtestEvaluation, false);
		
//		boolean result = changeIndicatorLength(backtestEvaluation, 30, IndicatorParametersForSTORSI.class);
//		
//		if (result){
//			new BacktestEvaluationWriter().writeToDatabase(backtestEvaluation, false);
//			Co.println("Rewrote evaluation OK");
//		}else{
//			Co.println("Failed to rewrite evaluation");
//		}
	}
	
	private boolean changeIndicatorLength(BacktestEvaluation backtestEvaluation, int length, Class clazz){
		for (IndicatorParameters indicatorParameters : backtestEvaluation.algorithmModel.listOfIndicatorParameters){
			if (indicatorParameters.getClass() == clazz){
				indicatorParameters.periodLength.value = length;
				return true;
			}
		}
		
		return false;
	}
}
