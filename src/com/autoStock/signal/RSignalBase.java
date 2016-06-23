/**
 * 
 */
package com.autoStock.signal;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;

/**
 * @author Kevin
 *
 */
public class RSignalBase extends SignalBase {
	public RSignalBase(SignalMetricType signalMetricType, SignalParameters signalParameters, AlgorithmBase algorithmBase) {
		super(signalMetricType, signalParameters, algorithmBase);
	}
}
