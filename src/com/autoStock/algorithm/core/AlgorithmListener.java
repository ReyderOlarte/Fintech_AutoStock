/**
 * 
 */
package com.autoStock.algorithm.core;

import java.util.Date;

import com.autoStock.strategy.StrategyResponse;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 *
 */
public interface AlgorithmListener {
	public void receiveStrategyResponse(StrategyResponse strategyResponse);
	public void receiveChangedStrategyResponse(StrategyResponse strategyResponse);
	public void receiveTick(QuoteSlice quoteSlice, int receiveIndex, int processedIndex, boolean processed);
	public void endOfAlgorithm();
	public void initialize(Date startingDate, Date endDate);
}
