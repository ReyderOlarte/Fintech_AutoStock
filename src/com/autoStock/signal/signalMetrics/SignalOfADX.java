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
public class SignalOfADX extends SignalBase {
	public SignalOfADX(SignalParameters signalParameters, AlgorithmBase algorithmBase){
		super(SignalMetricType.metric_adx, signalParameters, algorithmBase);
	}
}
