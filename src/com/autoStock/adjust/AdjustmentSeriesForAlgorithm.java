package com.autoStock.adjust;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.signal.SignalBase;

/**
 * @author Kevin Kowalewski
 *
 */
public class AdjustmentSeriesForAlgorithm extends AdjustmentCampaign {
	private AlgorithmBase algorithmBase; 
	
	public AdjustmentSeriesForAlgorithm(AlgorithmBase algorithmBase){
		super();
		this.algorithmBase = algorithmBase;
	}
	
	@Override
	protected void initializeAdjustmentCampaign() {
		
		
//		addTypicalSignalRange(algorithmBase.signalGroup.signalOfUO);
//     	listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("UO Period", algorithmBase.signalGroup.signalOfUO.signalParameters.periodLength, new IterableOfInteger(20, 30, 2, true)));
		
//		addTypicalSignalRange(algorithmBase.signalGroup.signalOfARUp);
//		addTypicalSignalRange(algorithmBase.signalGroup.signalOfARDown);
		
//		listOfAdjustmentBase.add(new AdjustmentOfSignalMetric(algorithmBase.signalGroup.signalOfCCI, AdjustmentType.signal_metric_long_entry, new IterableOfInteger(-30, 30, 3, false)));
//		listOfAdjustmentBase.add(new AdjustmentOfSignalMetric(algorithmBase.signalGroup.signalOfCCI, AdjustmentType.signal_metric_long_exit, new IterableOfInteger(-30, 30, 3, false)));
//		listOfAdjustmentBase.add(new AdjustmentOfSignalMetric(algorithmBase.signalGroup.signalOfCCI, AdjustmentType.signal_metric_short_entry, new IterableOfInteger(-30, 30, 3, false)));
//		listOfAdjustmentBase.add(new AdjustmentOfSignalMetric(algorithmBase.signalGroup.signalOfCCI, AdjustmentType.signal_metric_short_exit, new IterableOfInteger(-30, 30, 3, false)));

////		listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("CCI Average", SignalMetricType.metric_cci.maxSignalAverage, new IterableOfInteger(1, 10, 1)));
		
		listOfAdjustmentBase.add(new AdjustmentOfSignalMetricThreshold(algorithmBase.signalGroup.signalOfUO, AdjustmentType.signal_metric_long_entry, new IterableOfInteger(-30, 30, 1, false)));
		listOfAdjustmentBase.add(new AdjustmentOfSignalMetricThreshold(algorithmBase.signalGroup.signalOfUO, AdjustmentType.signal_metric_long_exit, new IterableOfInteger(-30, 30, 1, false)));
		listOfAdjustmentBase.add(new AdjustmentOfSignalMetricThreshold(algorithmBase.signalGroup.signalOfUO, AdjustmentType.signal_metric_short_entry, new IterableOfInteger(-30, 30, 1, false)));
		listOfAdjustmentBase.add(new AdjustmentOfSignalMetricThreshold(algorithmBase.signalGroup.signalOfUO, AdjustmentType.signal_metric_short_exit, new IterableOfInteger(-30, 30, 1, false)));
//		listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("UO Period", algorithmBase.signalGroup.signalOfUO.signalParameters.periodLength, new IterableOfInteger(20, 30, 5, true)));
//		listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("UO Average", algorithmBase.signalGroup.signalOfUO.signalParameters.maxSignalAverage, new IterableOfInteger(1, 10, 1)));
		
//		listOfAdjustmentBase.add(new AdjustmentOfEnum<SignalGuageType>("UO Guage Long Entry", new IterableOfEnum<SignalGuageType>(SignalGuageType.values()), algorithmBase.signalGroup.signalOfUO.signalParameters.arrayOfSignalGuageForLongEntry[0].mutableEnumForSignalGuageType));
//		listOfAdjustmentBase.add(new AdjustmentOfEnum<SignalGuageType>("UO Guage Long Exit", new IterableOfEnum<SignalGuageType>(SignalGuageType.values()), algorithmBase.signalGroup.signalOfUO.signalParameters.arrayOfSignalGuageForLongExit[0].mutableEnumForSignalGuageType));
//		listOfAdjustmentBase.add(new AdjustmentOfEnum<SignalGuageType>("UO Guage Short Entry", new IterableOfEnum<SignalGuageType>(SignalGuageType.values()), algorithmBase.signalGroup.signalOfUO.signalParameters.arrayOfSignalGuageForShortEntry[0].mutableEnumForSignalGuageType));
//		listOfAdjustmentBase.add(new AdjustmentOfEnum<SignalGuageType>("UO Guage Long Exit", new IterableOfEnum<SignalGuageType>(SignalGuageType.values()), algorithmBase.signalGroup.signalOfUO.signalParameters.arrayOfSignalGuageForShortEntry[0].mutableEnumForSignalGuageType));
		
//		new AdjustmentOfEnum<SignalGuageType>("Tag", );
		
//		listOfAdjustmentBase.add(new AdjustmentOfSignalMetric(algorithmBase.signalGroup.signalOfRSI, AdjustmentType.signal_metric_long_entry, new IterableOfInteger(-30, 0, 3)));
//		listOfAdjustmentBase.add(new AdjustmentOfSignalMetric(algorithmBase.signalGroup.signalOfRSI, AdjustmentType.signal_metric_long_exit, new IterableOfInteger(0, 30, 3)));
//		listOfAdjustmentBase.add(new AdjustmentOfSignalMetric(algorithmBase.signalGroup.signalOfRSI, AdjustmentType.signal_metric_short_entry, new IterableOfInteger(0, 30, 3)));
//		listOfAdjustmentBase.add(new AdjustmentOfSignalMetric(algorithmBase.signalGroup.signalOfRSI, AdjustmentType.signal_metric_short_exit, new IterableOfInteger(-30, 0, 3)));
//		listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("RSI Period", algorithmBase.signalGroup.signalOfRSI.signalParameters.periodLength, new IterableOfInteger(15, 45, 5)));
		
		
//		listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("CCI Period", algorithmBase.signalGroup.signalOfCCI.signalParameters.periodLength, new IterableOfInteger(15, 45, 5)));
//		
		
//		listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("intervalForReentryMins", algorithmBase.strategyBase.strategyOptions.intervalForReentryMins, new IterableOfInteger(1, 10, 1)));
//		listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("maxReenterTimes", algorithmBase.strategyBase.strategyOptions.maxReenterTimes, new IterableOfInteger(1, 10, 1)));

//		listOfAdjustmentBase.add(new AdjustmentOfBasicDouble("maxStopLossPercent", algorithmBase.strategyBase.strategyOptions.maxStopLossPercent, new IterableOfDouble(-1, 0, 0.1)));
//		listOfAdjustmentBase.add(new AdjustmentOfBasicDouble("maxProfitDrawdownPercent", algorithmBase.strategyBase.strategyOptions.maxProfitDrawdownPercent, new IterableOfDouble(-1, 0, 0.1)));

//		listOfAdjustmentBase.add(new AdjustmentOfBasicDouble("minReentryPercentGain", algorithmBase.strategyBase.strategyOptions.minReentryPercentGain, new IterableOfDouble(0.1, 0.5, 0.1)));
		
//		Co.println("--> " + algorithmBase.strategyBase.strategyOptions.maxStopLossPercent.toString());
		
//		Co.println("--> Check: " + algorithmBase.signalGroup.signalOfCCI.signalParameters.periodLength.toString());
	}
	
	private void addTypicalSignalRange(SignalBase signalBase){
		listOfAdjustmentBase.add(new AdjustmentOfSignalMetricThreshold(signalBase, AdjustmentType.signal_metric_long_entry, new IterableOfInteger(-30, 30, 2, false)));
		listOfAdjustmentBase.add(new AdjustmentOfSignalMetricThreshold(signalBase, AdjustmentType.signal_metric_long_exit, new IterableOfInteger(-30, 30, 2, false)));
		listOfAdjustmentBase.add(new AdjustmentOfSignalMetricThreshold(signalBase, AdjustmentType.signal_metric_short_entry, new IterableOfInteger(-30, 30, 2, false)));
		listOfAdjustmentBase.add(new AdjustmentOfSignalMetricThreshold(signalBase, AdjustmentType.signal_metric_short_exit, new IterableOfInteger(-30, 30, 2, false)));
	}
}
