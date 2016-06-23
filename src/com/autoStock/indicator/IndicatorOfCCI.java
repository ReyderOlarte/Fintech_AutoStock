/**
 * 
 */
package com.autoStock.indicator;

import com.autoStock.indicator.results.ResultsCCI;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfCCI extends IndicatorBase<ResultsCCI> {
	public IndicatorOfCCI(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}
	
	@Override
	public ResultsCCI analyze(){
		results = new ResultsCCI(indicatorParameters.resultSetLength);
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode = taLibCore.cci(0, endIndex, arrayOfPriceHigh, arrayOfPriceLow, arrayOfPriceClose, indicatorParameters.periodLength.value, new MInteger(), new MInteger(), results.arrayOfValue);
	
		handleAnalysisResult(returnCode);
		
		return results;
	}
}
