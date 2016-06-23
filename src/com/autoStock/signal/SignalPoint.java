package com.autoStock.signal;

import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalPointType;

/**
 * @author Kevin Kowalewski
 *
 */
public class SignalPoint {
	public int occurences;
	public SignalPointType signalPointType = SignalPointType.none;
	public SignalMetricType signalMetricType = SignalMetricType.none;
	
	public SignalPoint(){}
	
	public SignalPoint(SignalPointType signalPointType, SignalMetricType signalMetricType){
		this.signalPointType = signalPointType;
		this.signalMetricType = signalMetricType;
	}
}
