package com.autoStock.algorithm;

import com.autoStock.account.BasicAccount;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.strategy.StrategyOfTest;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class DummyAlgorithm extends AlgorithmBase {
	
	public DummyAlgorithm(Exchange exchange, Symbol symbol, AlgorithmMode algorithmMode, BasicAccount basicAccount) {
		super(exchange, symbol, algorithmMode, basicAccount);
		setStrategy(new StrategyOfTest(this));
	}

	@Override
	public void receiveQuoteSlice(QuoteSlice quoteSlice) {
		
	}

	@Override
	public void endOfFeed(Symbol symbol) {
		
	}
}
