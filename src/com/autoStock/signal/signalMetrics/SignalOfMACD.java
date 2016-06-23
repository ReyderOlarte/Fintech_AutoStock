/**
 * 
 */
package com.autoStock.signal.signalMetrics;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.indicator.results.ResultsBase;
import com.autoStock.indicator.results.ResultsMACD;
import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.autoStock.tools.ArrayTools;

/**
 * @author Kevin Kowalewski
 *
 */
public class SignalOfMACD extends SignalBase {
	public SignalOfMACD(SignalParameters signalParameters, AlgorithmBase algorithmBase){
		super(SignalMetricType.metric_macd, signalParameters, algorithmBase);
	}
	
	@Override
	public void setInput(ResultsBase resultsBase) {
		setInput(ArrayTools.getLast(((ResultsMACD)resultsBase).arrayOfMACDHistogram));
	}
}
