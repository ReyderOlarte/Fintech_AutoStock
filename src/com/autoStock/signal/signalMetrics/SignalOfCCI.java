/**
 * 
 */
package com.autoStock.signal.signalMetrics;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.autoStock.signal.SignalGroup;

/**
 * @author Kevin Kowalewski
 *
 */
public class SignalOfCCI extends SignalBase {	
	public SignalOfCCI(SignalParameters signalParameters, AlgorithmBase algorithmBase) {
		super(SignalMetricType.metric_cci, signalParameters, algorithmBase);
	}
}
