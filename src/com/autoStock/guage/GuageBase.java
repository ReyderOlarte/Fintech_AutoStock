package com.autoStock.guage;

import com.autoStock.signal.SignalBase;


/**
 * @author Kevin Kowalewski
 *
 */
public abstract class GuageBase {
	protected SignalGuage signalGuage;
	protected SignalBase signalBase;
	protected double[] arrayOfValues;
	public abstract boolean isQualified();
	
	public GuageBase(SignalBase signalBase, SignalGuage signalGuage, double[] arrayOfValues) {
		this.signalBase = signalBase;
		this.signalGuage = signalGuage;
		this.arrayOfValues = arrayOfValues;
	}
}
