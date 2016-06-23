/**
 * 
 */
package com.autoStock.signal;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.autoStock.trading.types.Position;

/**
 * @author Kevin
 *
 */
public class SignalOfReentry extends SignalBaseWithPoint {

	public SignalOfReentry(SignalMetricType signalMetricType, SignalParameters signalParameters, AlgorithmBase algorithmBase) {
		super(signalMetricType, signalParameters, algorithmBase);
	}
	
	@Override
	public void setInputCached(double strength, double normalizedValue, double rawValue) {throw new IllegalAccessError("Don't call this");}
	
	@Override
	public void setInput(double value) {throw new IllegalAccessError("Don't call this");}

	@Override
	public SignalPoint getSignalPoint(Position position) {
		return null;
	}

}
