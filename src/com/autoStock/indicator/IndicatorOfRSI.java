/**
 * 
 */
package com.autoStock.indicator;

import com.autoStock.indicator.results.ResultsRSI;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfRSI extends IndicatorBase<ResultsRSI>{
	public IndicatorOfRSI(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}
	
	@Override
	public ResultsRSI analyze(){
		results = new ResultsRSI(indicatorParameters.resultSetLength);
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode = taLibCore.rsi(0, endIndex, arrayOfPriceClose, indicatorParameters.periodLength.value-1, new MInteger(), new MInteger(), results.arrayOfValue);
		handleAnalysisResult(returnCode);
		
		return results;
	}
}
