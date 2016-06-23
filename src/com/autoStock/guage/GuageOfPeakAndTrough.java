package com.autoStock.guage;

import java.util.Arrays;

import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalDefinitions.SignalBounds;
import com.autoStock.tools.MathTools;

/**
 * @author Kevin Kowalewski
 *
 */
public class GuageOfPeakAndTrough extends GuageBase {
	private double currentValue;
	
	public GuageOfPeakAndTrough(SignalBase signalBase, SignalGuage signalGuage, double[] arrayOfValues) {
		super(signalBase, signalGuage, arrayOfValues);
		currentValue = arrayOfValues[arrayOfValues.length-1];
	}

	@Override
	public boolean isQualified() {
		if (arrayOfValues.length >= 10){
			if (signalGuage.signalBounds == SignalBounds.bounds_upper){
				return hasPeaked();
			}else if (signalGuage.signalBounds == SignalBounds.bounds_lower){
				return hasTroughed();
			}
		}
		
		return false;
	}
	
	private boolean hasPeaked(){
		boolean hasPeaked = false;
		
		if (getIndexOfMax() == 7){
			if (MathTools.isDecreasing(Arrays.copyOfRange(arrayOfValues, 6, 10), 1, false)){
				return true;
			}
		}
		
		return hasPeaked;
	}
	
	private boolean hasTroughed(){
		boolean hasTroughed = false;
		
		if (getIndexOfMin() == 6){
			if (MathTools.isIncreasing(Arrays.copyOfRange(arrayOfValues, 6, 10), 1, false)){
				return true;
			}
		}	
		
		return hasTroughed;
	}
	
	private int getIndexOfMax(){
		int maxIndex = 0;
		double maxValue = Double.NEGATIVE_INFINITY;
		
		for (int i=0; i<arrayOfValues.length; i++){
			double value = arrayOfValues[i];
			
			if (value >= maxValue){
				maxIndex = i;
				maxValue = value;
			}
		}
		
		return maxIndex;
	}
	
	private int getIndexOfMin(){
		int minIndex = 0;
		double minValue = Double.POSITIVE_INFINITY;
		
		for (int i=0; i<arrayOfValues.length; i++){
			double value = arrayOfValues[i];
			
			if (value <= minValue){
				minIndex = i;
				minValue = value;
			}
		}
		
		return minIndex;
	}
}
