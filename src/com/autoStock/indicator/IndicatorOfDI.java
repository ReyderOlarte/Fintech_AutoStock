/**
 * 
 */
package com.autoStock.indicator;

import com.autoStock.indicator.results.ResultsDI;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfDI extends IndicatorBase<ResultsDI> {
	public IndicatorOfDI(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}
	
	@Override
	public ResultsDI analyze(){
		results = new ResultsDI(indicatorParameters.resultSetLength);
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode = taLibCore.plusDI(0, endIndex, arrayOfPriceHigh, arrayOfPriceLow, arrayOfPriceClose, indicatorParameters.periodLength.value-1, new MInteger(), new MInteger(), results.arrayOfDIPlus);
		if (returnCode == RetCode.Success){
			returnCode = taLibCore.minusDI(0, endIndex, arrayOfPriceHigh, arrayOfPriceLow, arrayOfPriceClose, indicatorParameters.periodLength.value-1, new MInteger(), new MInteger(), results.arrayOfDIMinus);
		}
		handleAnalysisResult(returnCode);
		
		return results;
	}
}
