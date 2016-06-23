/**
 * 
 */
package com.autoStock.indicator;

import com.autoStock.r.RJavaController;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;

/**
 * @author Kevin
 * @param <T>
 *
 */
public abstract class RIndicatorBase<T> extends IndicatorBase<T> {
	public RIndicatorBase(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}
}
