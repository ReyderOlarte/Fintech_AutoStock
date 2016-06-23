/**
 * 
 */
package com.autoStock.indicator;

import java.util.Arrays;

import org.rosuda.JRI.Rengine;

import com.autoStock.Co;
import com.autoStock.indicator.results.ResultsCCI;
import com.autoStock.r.RJavaController;
import com.autoStock.r.RUtils;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.tools.ArrayTools;

/**
 * @author Kevin
 *
 */
public class RIndicatorOfCCI extends RIndicatorBase<ResultsCCI> {
	public RIndicatorOfCCI(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
	}

	@Override
	public ResultsCCI analyze() {
		Rengine r = RJavaController.getInstance().getREngine();
		
		double[][] matrix = ArrayTools.toMatrix(arrayOfPriceHigh, arrayOfPriceLow, arrayOfPriceClose);
		
		RUtils.assignAsRMatrix(r, matrix, getClass().getSimpleName());
		
//		for (double value : Arrays.copyOfRange(r.eval("CCI(a)").asDoubleArray(), 19, 30)){Co.println("--> Value: " + value);}		
		results = new ResultsCCI(indicatorParameters.resultSetLength);
		results.arrayOfDates = arrayOfDates;
		results.arrayOfValue = Arrays.copyOfRange(r.eval("CCI(" + getClass().getSimpleName() + ")").asDoubleArray(), 29, 30);
		
		return results;
	}
}
