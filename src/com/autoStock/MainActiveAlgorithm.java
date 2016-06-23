package com.autoStock;

import com.autoStock.algorithm.core.ActiveAlgorithmContainer;
import com.autoStock.algorithm.core.ActiveAlgorithmContainer.ActivationListener;
import com.autoStock.internal.Global;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class MainActiveAlgorithm implements ActivationListener {
	private final ActiveAlgorithmContainer activeAlgorithmContainer;
	
	public MainActiveAlgorithm(Exchange exchange, Symbol symbol) {
		Global.callbackLock.requestLock();
		
		activeAlgorithmContainer = new ActiveAlgorithmContainer(exchange, symbol, null, this);
		activeAlgorithmContainer.activate();
	}

	@Override
	public void activated(ActiveAlgorithmContainer activeAlgorithmContainer) {
		Co.println("--> Activation success!");
	}

	@Override
	public void failed(ActiveAlgorithmContainer activeAlgorithmContainer, String reason) {
		Co.println("--> Activation failure!");
		
		Global.callbackLock.releaseLock();
	}
}
