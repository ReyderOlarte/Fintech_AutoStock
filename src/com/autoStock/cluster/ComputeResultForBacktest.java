package com.autoStock.cluster;

import java.util.ArrayList;
import java.util.HashMap;

import com.autoStock.backtest.BacktestEvaluation;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class ComputeResultForBacktest {
	public long requestId;	
	public HashMap<Symbol, ArrayList<BacktestEvaluation>> hashOfBacktestEvaluation;
	
	public ComputeResultForBacktest(long requestId, HashMap<Symbol, ArrayList<BacktestEvaluation>> hashOfBacktestEvaluation) {
		this.requestId = requestId;
		this.hashOfBacktestEvaluation = hashOfBacktestEvaluation;
	}
}
