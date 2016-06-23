/**
 * 
 */
package com.autoStock.signal;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.autoStock.signal.extras.EncogFrameSupport.EncogFrameSource;

/**
 * @author Kevin
 *
 */
public abstract class SignalBaseWithEF extends SignalBase implements EncogFrameSource {
	public SignalBaseWithEF(SignalMetricType signalMetricType, SignalParameters signalParameters, AlgorithmBase algorithmBase) {
		super(signalMetricType, signalParameters, algorithmBase);
	}
}
