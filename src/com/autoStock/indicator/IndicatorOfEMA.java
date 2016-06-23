/**
 * 
 */
package com.autoStock.indicator;

import com.autoStock.Co;
import com.autoStock.indicator.results.ResultsEMA;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfEMA extends IndicatorBase<ResultsEMA> {
	private boolean isDema;
	
	public IndicatorOfEMA(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType, boolean isDema) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
		this.isDema = isDema;
	}
	
	@Override
	public ResultsEMA analyze(){
		results = new ResultsEMA(indicatorParameters.resultSetLength);
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode;
		
		if (isDema){
			returnCode = taLibCore.dema(0, endIndex, arrayOfPriceClose, indicatorParameters.periodLength.value, new MInteger(), new MInteger(), results.arrayOfValue);			
		}else{
			returnCode = taLibCore.ema(0, endIndex, arrayOfPriceClose, indicatorParameters.periodLength.value, new MInteger(), new MInteger(), results.arrayOfValue);
		}
	
		handleAnalysisResult(returnCode);
		
		return results;
	}
}
