/**
 * 
 */
package com.autoStock.indicator;

import com.autoStock.indicator.results.ResultsSMA;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfSMA extends IndicatorBase<ResultsSMA> {
	public IndicatorOfSMA(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}
	
	@Override
	public ResultsSMA analyze(){
		results = new ResultsSMA(indicatorParameters.resultSetLength);
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode = taLibCore.sma(0, endIndex, arrayOfPriceClose, indicatorParameters.periodLength.value, new MInteger(), new MInteger(), results.arrayOfValue);
		handleAnalysisResult(returnCode);
		
		return results;
	}
}
