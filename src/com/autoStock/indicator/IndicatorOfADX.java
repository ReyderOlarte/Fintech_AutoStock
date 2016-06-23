/**
 * 
 */
package com.autoStock.indicator;

import com.autoStock.indicator.results.ResultsADX;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfADX extends IndicatorBase<ResultsADX> {	
	public IndicatorOfADX(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}

	@Override
	public ResultsADX analyze(){
		results = new ResultsADX(indicatorParameters.resultSetLength);
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode = taLibCore.adx(0, endIndex -1, arrayOfPriceHigh, arrayOfPriceLow, arrayOfPriceClose, indicatorParameters.periodLength.value/2, new MInteger(), new MInteger(), results.arrayOfValue);
		handleAnalysisResult(returnCode);
		
		return results;
	}
}
