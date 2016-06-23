/**
 * 
 */
package com.autoStock.indicator;

import java.util.Arrays;

import com.autoStock.indicator.results.ResultsWILLR;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfWILLR extends IndicatorBase<ResultsWILLR> {
	public IndicatorOfWILLR(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}
	
	@Override
	public ResultsWILLR analyze(){
		results = new ResultsWILLR(indicatorParameters.resultSetLength+1); //WILLR Specific
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode = taLibCore.willR(0, endIndex, arrayOfPriceHigh, arrayOfPriceLow, arrayOfPriceClose, indicatorParameters.periodLength.value-1, new MInteger(), new MInteger(), results.arrayOfValue);
		
		results.arrayOfValue = Arrays.copyOfRange(results.arrayOfValue, 1, indicatorParameters.resultSetLength +1); //WILLR specific
		
		handleAnalysisResult(returnCode);
		
		return results;
	}
}
