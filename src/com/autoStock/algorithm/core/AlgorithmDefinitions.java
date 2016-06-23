package com.autoStock.algorithm.core;

import com.autoStock.backtest.BacktestDefinitions.BacktestType;

/**
 * @author Kevin Kowalewski
 *
 */
public class AlgorithmDefinitions {
	public static enum AlgorithmMode {
		mode_backtest(true, true, false, true),
		mode_backtest_with_adjustment(false, false, false, false),
		mode_backtest_single_with_tables(false, false, false, true),
		mode_backtest_single_no_tables(false, false, false, false),
		mode_engagement(false, true, false, true),
		mode_backtest_silent(false, false, false, true);
		;
		
		public boolean displayChart;
		public boolean displayTable;
		public boolean displayMessages;
		public boolean populateTable;
		
		AlgorithmMode(boolean displayChart, boolean displayTable, boolean displayMessages, boolean populateTable){
			this.displayChart = displayChart;
			this.displayTable = displayTable;
			this.displayMessages = displayMessages;
			this.populateTable = populateTable;
		}

		public static AlgorithmMode getFromBacktestType(BacktestType backtestType) {
			if (backtestType == BacktestType.backtest_default || backtestType == BacktestType.backtest_bgi){
				return AlgorithmMode.mode_backtest;
			}else if (backtestType == BacktestType.backtest_adjustment_boilerplate || backtestType == BacktestType.backtest_adjustment_individual){
				return AlgorithmMode.mode_backtest_with_adjustment;	
			}else if (backtestType == BacktestType.backtest_clustered_client){
				return AlgorithmMode.mode_backtest_with_adjustment;
			}else if (backtestType == BacktestType.backtest_result_only){
				return AlgorithmMode.mode_backtest_silent;
			}
			
			throw new UnsupportedOperationException();
		}
	}
}
