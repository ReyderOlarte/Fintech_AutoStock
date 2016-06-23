package com.autoStock.backtest;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public interface ListenerOfBacktest {	
	public void onCompleted(Symbol symbol, AlgorithmBase algorithmBase);
}