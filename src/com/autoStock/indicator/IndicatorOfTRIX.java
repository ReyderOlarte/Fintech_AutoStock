/**
 * 
 */
package com.autoStock.indicator;

import java.util.Arrays;

import com.autoStock.indicator.results.ResultsTRIX;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfTRIX extends IndicatorBase<ResultsTRIX> {
	public IndicatorOfTRIX(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibcore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibcore, signalMetricType);
	}
	
	@Override
	public ResultsTRIX analyze(){
		results = new ResultsTRIX(indicatorParameters.resultSetLength+1); //TRIX Specific
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode = taLibCore.trix(0, endIndex, arrayOfPriceClose, indicatorParameters.periodLength.value/3, new MInteger(), new MInteger(), results.arrayOfValue);
		
		results.arrayOfValue = Arrays.copyOfRange(results.arrayOfValue, 1, indicatorParameters.resultSetLength +1); //TRIX Specific
		
		handleAnalysisResult(returnCode);
		
		return results;
	}
}
