package com.autoStock.signal;

import java.util.ArrayList;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.indicator.CommonAnalysisData;
import com.autoStock.indicator.results.ResultsBase;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.autoStock.taLib.Core;
import com.autoStock.tools.ArrayTools;
import com.autoStock.tools.ListTools;
import com.autoStock.trading.types.Position;
import com.autoStock.types.basic.MutableInteger;

public abstract class SignalBase {
	public SignalMetricType signalMetricType = SignalMetricType.none;
	protected ArrayList<Double> listOfNormalizedValue = new ArrayList<Double>();
	protected ArrayList<Double> listOfNormalizedAveragedValue = new ArrayList<Double>();
	public ArrayList<Double> listOfNormalizedValuePersist = new ArrayList<Double>();
	protected ArrayList<Double> listOfNormalizedAveragedValuePersist = new ArrayList<Double>();
	protected ArrayList<Double> listOfRawValuePersist = new ArrayList<Double>();
	protected MutableInteger maxSignalAverage;
	public SignalParameters signalParameters;
	public transient AlgorithmBase algorithmBase;
	public transient SignalRangeLimit signalRangeLimit = new SignalRangeLimit();
	public transient CommonAnalysisData commonAnalysisData;
	public transient Core taLibCore;
	
	public static interface SignalExtra {
		public String toExtra();
		public void fromExtra(String extra);
	}
	
	public SignalBase(SignalMetricType signalMetricType, SignalParameters signalParameters, AlgorithmBase algorithmBase){
		this.algorithmBase = algorithmBase;
		this.signalMetricType = signalMetricType;
		this.signalParameters = signalParameters;
		maxSignalAverage = signalParameters.maxSignalAverage;
	}
	
	public double[] getStrengthWindow(){
		return ArrayTools.getArrayFromListOfDouble(listOfNormalizedAveragedValue);
	}
	
	public double[] getNormalizedAveragedWindow(int windowSize){
		if (windowSize <= 0 || listOfNormalizedAveragedValuePersist.size() - windowSize < 0){throw new IllegalArgumentException();}
		return ArrayTools.getArrayFromListOfDouble(listOfNormalizedAveragedValuePersist.subList(listOfNormalizedAveragedValuePersist.size()-windowSize, listOfNormalizedAveragedValuePersist.size()));
	}
	
	public double[] getNormalizedWindow(int windowSize){
		if (windowSize <= 0 || listOfNormalizedValuePersist.size() - windowSize < 0){throw new IllegalArgumentException("Could not satisfy window size: " + windowSize + ", available: " + listOfNormalizedValuePersist.size());}
		return ArrayTools.getArrayFromListOfDouble(ListTools.getLast(listOfNormalizedValuePersist, windowSize));
	}
	
	public double[] getRawWindow(int windowSize){
		if (windowSize <= 0 || listOfRawValuePersist.size() - windowSize < 0){throw new IllegalArgumentException("Could not satisfy window size: " + windowSize + ", available: " + listOfRawValuePersist.size());}
		return ArrayTools.getArrayFromListOfDouble(ListTools.getLast(listOfRawValuePersist, windowSize));
	}

	public double getStrength(){
		if (listOfNormalizedValue.size() == 0){return 0;}
//		Co.println("--> Size: " + listOfNormalizedValue.size() + ", " +maxSignalAverage.value + this.getClass().getName());

		double normalizedValue = 0;
		int normalizationSize = Math.min(maxSignalAverage.value, listOfNormalizedValue.size());
		
		for (int i=0; i<normalizationSize; i++){
			normalizedValue += listOfNormalizedValue.get(listOfNormalizedValue.size() - i -1);
		}
		
		return normalizedValue / normalizationSize;
//		return listOfNormalizedValue.get(listOfNormalizedValue.size()-1);
	}
	
	public SignalPoint getSignalPoint(Position position){
		return new SignalPointResolver(this).getSignalPoint(position);
	}
	
	public void setInputCached(double strength, double normalizedValue, double rawValue){
		listOfNormalizedValue.add(normalizedValue);
		listOfNormalizedValuePersist.add(normalizedValue);
		
		listOfNormalizedAveragedValue.add(strength);
		listOfNormalizedAveragedValuePersist.add(strength);
		listOfRawValuePersist.add(rawValue);
		signalRangeLimit.addValue(strength);
		
		prune(maxSignalAverage.value);
	}
	
	public void setInput(ResultsBase resultsBase){
		setInput(ArrayTools.getLast(resultsBase.arrayOfValue));
	}
	
	public void setInput(double value){
		double normalizedValue = signalParameters.normalizeInterface.normalize(value);
		
		listOfNormalizedValue.add(normalizedValue);
		listOfNormalizedValuePersist.add(normalizedValue);
		
		double strength = getStrength();
		
		listOfNormalizedAveragedValue.add(strength);
		listOfNormalizedAveragedValuePersist.add(strength);
		listOfRawValuePersist.add(value);
		signalRangeLimit.addValue(strength);
		
		prune(maxSignalAverage.value);
	}
	
	private void prune(int toLength){
		while (listOfNormalizedValue.size() > toLength){
			listOfNormalizedValue.remove(0);
		}
		
		while (listOfNormalizedAveragedValue.size() > toLength){
			listOfNormalizedAveragedValue.remove(0);
		}
	}
	
	public double getMiddle(){
		double middle = 0;
		
		for (Double value : listOfNormalizedAveragedValuePersist){
			middle += value;
		}
		
		return middle / listOfNormalizedAveragedValuePersist.size();
	}

	public void reset() {
		listOfNormalizedAveragedValuePersist.clear();
		listOfNormalizedAveragedValue.clear();
		listOfNormalizedValuePersist.clear();
		listOfNormalizedValue.clear();
		listOfRawValuePersist.clear();
		signalRangeLimit.reset();
	}

	public void setCommonAnalysisData(CommonAnalysisData commonAnalysisData) {
		this.commonAnalysisData = commonAnalysisData;
	}

	public void setTaLib(Core taLibCore) {
		this.taLibCore = taLibCore;
	}
}
