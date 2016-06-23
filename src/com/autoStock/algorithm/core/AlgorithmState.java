package com.autoStock.algorithm.core;

/**
 * @author Kevin Kowalewski
 *
 */
public class AlgorithmState {
	public boolean isDisabled;
	public int transactions;
	public String disabledReason;
	
	public AlgorithmState(){
		reset();
	}
	
	public void reset() {
		isDisabled = false;
		transactions = 0;
		disabledReason = null;
	}
}
