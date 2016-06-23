/**
 * 
 */
package com.autoStock.indicator;

import com.autoStock.indicator.results.ResultsBB;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MAType;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfBB extends IndicatorBase<ResultsBB> {
	public int optionDeviationUp = 8;
	public int optionDeviationDown = 8;
	
	public IndicatorOfBB(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}
	
	@Override
	public ResultsBB analyze(){	
		results = new ResultsBB(indicatorParameters.resultSetLength);
		
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode = taLibCore.bbands(0, endIndex, arrayOfPriceClose, indicatorParameters.periodLength.value, optionDeviationUp, optionDeviationDown, MAType.Kama, new MInteger(), new MInteger(), results.arrayOfUpperBand, results.arrayOfMiddleBand, results.arrayOfLowerBand);
		handleAnalysisResult(returnCode);
		
		return results;
	}
}
