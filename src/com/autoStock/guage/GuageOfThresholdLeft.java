package com.autoStock.guage;

import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalDefinitions.SignalBounds;
import com.autoStock.tools.ArrayTools;

/**
 * @author Kevin Kowalewski
 *
 */
public class GuageOfThresholdLeft extends GuageBase {
	public GuageOfThresholdLeft(SignalBase signalBase, SignalGuage signalGuage, double[] arrayOfValues) {
		super(signalBase, signalGuage, arrayOfValues);
	}

	@Override
	public boolean isQualified(){
		boolean isQualifiedMet =  arrayOfValues.length > 1 && new GuageOfThresholdMet(signalBase, signalGuage, ArrayTools.subArray(arrayOfValues, 0, arrayOfValues.length-1)).isQualified();
		
		if (signalGuage.signalBounds == SignalBounds.bounds_upper){
			if (arrayOfValues[arrayOfValues.length-1] < signalGuage.threshold && isQualifiedMet){
				return true;
			}
		} else {
			if (arrayOfValues[arrayOfValues.length-1] > signalGuage.threshold && isQualifiedMet){
				return true;
			}
		}
		
		return false;
	}
}
