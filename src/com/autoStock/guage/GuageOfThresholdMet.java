package com.autoStock.guage;

import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalDefinitions.SignalBounds;

/**
 * @author Kevin Kowalewski
 *
 */
public class GuageOfThresholdMet extends GuageBase {
	public GuageOfThresholdMet(SignalBase signalBase, SignalGuage signalGuage, double[] arrayOfValues) {
		super(signalBase, signalGuage, arrayOfValues);
	}

	@Override
	public boolean isQualified(){
		if (signalGuage.signalBounds == SignalBounds.bounds_upper){
			if (arrayOfValues[arrayOfValues.length-1] >= signalGuage.threshold){
				return true;
			}
		} else if (signalGuage.signalBounds == SignalBounds.bounds_lower) {
			if (arrayOfValues[arrayOfValues.length-1] <= signalGuage.threshold){
				return true;
			}
		}else{
			throw new UnsupportedOperationException();
		}
		
		return false;
	}
}
