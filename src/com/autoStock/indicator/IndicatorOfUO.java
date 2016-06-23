/**
 * 
 */
package com.autoStock.indicator;

import com.autoStock.indicator.results.ResultsUO;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfUO extends IndicatorBase<ResultsUO> {
	public IndicatorOfUO(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}
	
	@Override
	public ResultsUO analyze(){
		results = new ResultsUO(indicatorParameters.resultSetLength);
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode = taLibCore.ultOsc(0, endIndex, arrayOfPriceHigh, arrayOfPriceLow, arrayOfPriceClose, 7, 14, endIndex-indicatorParameters.resultSetLength+1, new MInteger(), new MInteger(), results.arrayOfValue);
		
		handleAnalysisResult(returnCode);
		
		return results;
	}
}
