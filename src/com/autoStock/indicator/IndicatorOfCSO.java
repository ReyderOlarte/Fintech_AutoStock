/**
 * 
 */
package com.autoStock.indicator;

import java.util.Date;

import com.autoStock.indicator.IndicatorOfCSO.ResultsCSO;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;

/**
 * @author Kevin
 *
 */
public class IndicatorOfCSO extends IndicatorBase<ResultsCSO> {
	private double firstPriceClose;
	
	public static class ResultsCSO {
		public Date[] arrayOfDates;
		public double[] arrayOfCSO;
		
		public ResultsCSO(int length){
			this.arrayOfDates = new Date[length];
			this.arrayOfCSO = new double[length];
		}
	}
	
	public IndicatorOfCSO(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}

	@Override
	public ResultsCSO analyze() {
		ResultsCSO results = new ResultsCSO(1);
		if (firstPriceClose == 0){firstPriceClose = arrayOfPriceClose[arrayOfPriceClose.length-1];}
		results.arrayOfCSO[0] = arrayOfPriceClose[arrayOfPriceClose.length-1] - firstPriceClose;
		return results;
	}

}
