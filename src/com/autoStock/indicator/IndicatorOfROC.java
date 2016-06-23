/**
 * 
 */
package com.autoStock.indicator;

import com.autoStock.indicator.results.ResultsROC;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfROC extends IndicatorBase<ResultsROC>{
	public IndicatorOfROC(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}
	
	@Override
	public ResultsROC analyze(){
		results = new ResultsROC(indicatorParameters.resultSetLength);
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode = taLibCore.roc(0, endIndex, arrayOfPriceClose, indicatorParameters.periodLength.value-1, new MInteger(), new MInteger(), results.arrayOfValue);
		handleAnalysisResult(returnCode);
		
		return results;
	}
}
