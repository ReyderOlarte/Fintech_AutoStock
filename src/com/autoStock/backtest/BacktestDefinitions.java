/**
 * 
 */
package com.autoStock.backtest;

/**
 * @author kevink
 *
 */
public class BacktestDefinitions {
	public static enum BacktestType {
		backtest_default,
		backtest_adjustment_boilerplate,
		backtest_adjustment_individual,
		backtest_clustered_client,
		backtest_result_only,
		backtest_bgi,
	}
}