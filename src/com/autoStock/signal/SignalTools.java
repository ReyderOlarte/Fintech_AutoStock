package com.autoStock.signal;


/**
 * @author Kevin Kowalewski
 *
 */
public class SignalTools {
//	public static synchronized CombinedSignal getCombinedSignal(Signal signal){
//		ArrayList<SignalMetric> listOfSignalMetric = signal.getListOfSignalMetric();
//		CombinedSignal combinedSignal = new CombinedSignal();
//		int listOfSignalMetricSize = listOfSignalMetric.size();
//		
//		for (SignalMetric signalMetric : listOfSignalMetric){
//			combinedSignal.strength += signalMetric.getStrength();
//			combinedSignal.longEntry += signalMetric.signalMetricType.pointToSignalLongEntry;
//			combinedSignal.longExit += signalMetric.signalMetricType.pointToSignalLongExit;
//			combinedSignal.shortEntry += signalMetric.signalMetricType.pointToSignalShortExit;
//			combinedSignal.shortExit += signalMetric.signalMetricType.pointToSignalShortExit;
//		}
//		
//		if (listOfSignalMetricSize > 0){ 
//			combinedSignal.strength = combinedSignal.strength / listOfSignalMetricSize;
//			combinedSignal.longEntry = combinedSignal.longEntry / listOfSignalMetricSize;
//			combinedSignal.longExit = combinedSignal.longExit / listOfSignalMetricSize;
//			combinedSignal.shortEntry = combinedSignal.shortEntry / listOfSignalMetricSize;
//			combinedSignal.shortExit = combinedSignal.shortExit / listOfSignalMetricSize;
//		}
//		
//		combinedSignal.strength = (int) (combinedSignal.strength > 0 ? Math.min(100, combinedSignal.strength) : Math.max(-100, combinedSignal.strength));
//		
//		return combinedSignal;
//	}
}
