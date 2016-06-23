package com.autoStock.indicator;

import com.autoStock.indicator.results.ResultsMACD;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;
import com.autoStock.types.basic.MutableInteger;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndicatorOfMACD extends IndicatorBase<ResultsMACD> {
	public static MutableInteger immutableIntegerForLong = new MutableInteger(29);
	public static MutableInteger immutableIntegerForEma = new MutableInteger(9);
	public static MutableInteger immutableIntegerForShort = new MutableInteger(6);
	
	public IndicatorOfMACD(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}
	
	@Override
	public ResultsMACD analyze(){
		results = new ResultsMACD(indicatorParameters.resultSetLength);
		results.arrayOfDates = arrayOfDates;
		
		RetCode returnCode;
		
		returnCode = taLibCore.macd(0, endIndex, arrayOfPriceClose, 6, 22, 9, new MInteger(), new MInteger(), results.arrayOfMACD, results.arrayOfMACDSignal, results.arrayOfMACDHistogram);
		
		handleAnalysisResult(returnCode);
		return results;
	}
}
