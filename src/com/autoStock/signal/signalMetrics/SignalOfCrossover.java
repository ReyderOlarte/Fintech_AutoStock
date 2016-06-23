package com.autoStock.signal.signalMetrics;

import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.indicator.IndicatorOfEMA;
import com.autoStock.indicator.results.ResultsBase;
import com.autoStock.internal.GsonProvider;
import com.autoStock.misc.Pair;
import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalBaseWithPoint;
import com.autoStock.signal.SignalBase.SignalExtra;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.autoStock.signal.SignalDefinitions.SignalPointType;
import com.autoStock.signal.SignalPoint;
import com.autoStock.trading.types.Position;
import com.autoStock.types.basic.MutableInteger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author Kevin Kowalewski
 *
 */
public class SignalOfCrossover extends SignalBaseWithPoint implements SignalExtra {
	public double ema1Value = 0;
	public double ema2Value = 0;
	public IndicatorParametersForEMAFirst ipEMA1 = new IndicatorParametersForEMAFirst();
	public IndicatorParametersForEMASecond ipEMA2 = new IndicatorParametersForEMASecond();
	
	public SignalOfCrossover(SignalMetricType signalMetricType, SignalParameters signalParameters, AlgorithmBase algorithmBase) {
		super(signalMetricType, signalParameters, algorithmBase);
	}
	
	@Override
	public void setInput(double value) {
		IndicatorOfEMA ema1 = new IndicatorOfEMA(ipEMA1, commonAnalysisData, taLibCore, SignalMetricType.metric_crossover, false);
		IndicatorOfEMA ema2 = new IndicatorOfEMA(ipEMA2, commonAnalysisData, taLibCore, SignalMetricType.metric_crossover, false);
		
		//Co.println("--> EMAX: " + ipEMA1.periodLength.hashCode());
		//Co.println("--> EMA: " + ipEMA1.periodLength.value);
		
		ema1Value = ((ResultsBase)ema1.setDataSet().analyze()).getLast();
		ema2Value = ((ResultsBase)ema2.setDataSet().analyze()).getLast();
		
		super.setInput(ema1Value - ema2Value);
	}

	@Override
	public SignalPoint getSignalPoint(Position position) {
		double value = getStrength();
		
		if (hasCrossed() == false){
			return new SignalPoint();
		}
		
		if (position == null){
			if (value >= signalParameters.arrayOfSignalGuageForLongEntry[0].threshold){return new SignalPoint(SignalPointType.long_entry, SignalMetricType.metric_crossover);}
			if (value <= signalParameters.arrayOfSignalGuageForShortEntry[0].threshold){return new SignalPoint(SignalPointType.short_entry, SignalMetricType.metric_crossover);}
		}else{
			if (value <= signalParameters.arrayOfSignalGuageForShortExit[0].threshold && position.isLong()){return new SignalPoint(SignalPointType.long_exit, SignalMetricType.metric_crossover);}
			if (value >= signalParameters.arrayOfSignalGuageForLongExit[0].threshold && position.isShort()){return new SignalPoint(SignalPointType.short_exit, SignalMetricType.metric_crossover);}
		}
		
		return new SignalPoint();
	}
	
	private boolean hasCrossed(){
		for (double value : listOfNormalizedValuePersist){
			if (value >= 0 && value <= 1){
				return true;
			}
		}
		
		return false;
	}
	
	public static class IndicatorParametersForEMAFirst extends IndicatorParameters {
		public IndicatorParametersForEMAFirst() {super(new MutableInteger(5), 1);}
	}
	
	public static class IndicatorParametersForEMASecond extends IndicatorParameters {
		public IndicatorParametersForEMASecond() {super(new MutableInteger(34), 1);}
	}

	@Override
	public String toExtra() {
		return new Gson().toJson(new Pair<IndicatorParametersForEMAFirst, IndicatorParametersForEMASecond>(ipEMA1, ipEMA2));
	}

	@Override
	public void fromExtra(String extra) {
		//Co.println("--> Got string: " + extra);
		Pair<IndicatorParametersForEMAFirst, IndicatorParametersForEMASecond> pair = new Gson().fromJson(extra, new TypeToken<Pair<IndicatorParametersForEMAFirst, IndicatorParametersForEMASecond>>(){}.getType());
		ipEMA1 = pair.first;
		ipEMA2 = pair.second;
		signalParameters.periodLength.value = Math.max(ipEMA1.periodLength.value, ipEMA2.periodLength.value);
	}
}
