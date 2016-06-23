/**
 * 
 */
package com.autoStock.indicator;

import com.autoStock.indicator.results.ResultsMFI;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;
import com.autoStock.tools.ArrayTools;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfMFI extends IndicatorBase<ResultsMFI>{
	public IndicatorOfMFI(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}
	
	@Override
	public ResultsMFI analyze(){
		results = new ResultsMFI(indicatorParameters.resultSetLength);
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode = taLibCore.mfi(0, endIndex, arrayOfPriceHigh, arrayOfPriceLow, arrayOfPriceClose, ArrayTools.convertToDouble(arrayOfSizeVolume), indicatorParameters.periodLength.value-1, new MInteger(), new MInteger(), results.arrayOfValue);
		handleAnalysisResult(returnCode);
		
		return results;
	}
}
