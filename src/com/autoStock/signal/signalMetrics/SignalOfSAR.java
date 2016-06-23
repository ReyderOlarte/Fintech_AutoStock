/**
 * 
 */
package com.autoStock.signal.signalMetrics;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;

/**
 * @author Kevin Kowalewski
 *
 */
public class SignalOfSAR extends SignalBase {
	public SignalOfSAR(SignalParameters signalParameters, AlgorithmBase algorithmBase){
		super(SignalMetricType.metric_sar, signalParameters, algorithmBase);
	}
}
