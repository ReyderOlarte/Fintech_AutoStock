/**
 * 
 */
package com.autoStock.signal.signalMetrics;

import java.text.DecimalFormat;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.internal.ApplicationStates;
import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalBaseWithEF;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.autoStock.signal.extras.EncogFrame;
import com.autoStock.signal.extras.EncogFrame.FrameType;
import com.autoStock.signal.extras.EncogSubframe;
import com.autoStock.tools.ArrayTools;
import com.autoStock.tools.ListTools;

/**
 * @author Kevin Kowalewski
 * 
 */
public class SignalOfPPC extends SignalBaseWithEF {
	private static final int PERIOD_LENGTH = 31;
	
	public SignalOfPPC(SignalParameters signalParameters, AlgorithmBase algorithmBase) {
		super(SignalMetricType.metric_ppc, signalParameters, algorithmBase);
		signalParameters.periodLength.value = PERIOD_LENGTH;
	}

	@Override
	public EncogFrame asEncogFrame() {
		EncogFrame encogFrame = new EncogFrame(getClass().getSimpleName(), FrameType.percent_change);
		
		//Co.println("Last 30");
		
		double pero[] = new double[PERIOD_LENGTH-1];
		double perh[] = new double[PERIOD_LENGTH-1];
		double perl[] = new double[PERIOD_LENGTH-1];
		double perc[] = new double[PERIOD_LENGTH-1];
		double peroc[] = new double[PERIOD_LENGTH-1];
		double perlh[] = new double[PERIOD_LENGTH-1];
		
		int c = 0;
		
		for (int i=PERIOD_LENGTH; i>1; i--){
			//Co.println("--> " + commonAnalysisData.arrayOfPriceClose[i]);
			
			pero[c] = (commonAnalysisData.arrayOfPriceOpen[i-1] / commonAnalysisData.arrayOfPriceOpen[i]) -1;
			perh[c] = (commonAnalysisData.arrayOfPriceHigh[i-1] / commonAnalysisData.arrayOfPriceHigh[i]) -1;
			perl[c] = (commonAnalysisData.arrayOfPriceLow[i-1] / commonAnalysisData.arrayOfPriceLow[i]) -1;
			perc[c] = (commonAnalysisData.arrayOfPriceClose[i-1] / commonAnalysisData.arrayOfPriceClose[i]) -1;
			
			peroc[c] = (commonAnalysisData.arrayOfPriceOpen[i] / commonAnalysisData.arrayOfPriceClose[i]) -1;
			perlh[c] = (commonAnalysisData.arrayOfPriceLow[i] / commonAnalysisData.arrayOfPriceHigh[i]) -1;
			
			c++;
		}
		
//		for (int i=0; i<perc.length; i++){
//			Co.println("--> ");
//			Co.println("--> " + new DecimalFormat("#.########").format(pero[i]));
//			Co.println("--> " + new DecimalFormat("#.########").format(perh[i]));
//			Co.println("--> " + new DecimalFormat("#.########").format(perl[i]));
//			Co.println("--> " + new DecimalFormat("#.########").format(perc[i]));
//		}
//		
//		ApplicationStates.shutdown();
		
		EncogSubframe esf1 = new EncogSubframe(getClass().getSimpleName(), ListTools.getListFromArray(pero), FrameType.percent_change, 0.050, -0.050);
		EncogSubframe esf2 = new EncogSubframe(getClass().getSimpleName(), ListTools.getListFromArray(perh), FrameType.percent_change, 0.050, -0.050);
		EncogSubframe esf3 = new EncogSubframe(getClass().getSimpleName(), ListTools.getListFromArray(perl), FrameType.percent_change, 0.050, -0.050);
		EncogSubframe esf4 = new EncogSubframe(getClass().getSimpleName(), ListTools.getListFromArray(perc), FrameType.percent_change, 0.050, -0.050);
		EncogSubframe esf5 = new EncogSubframe(getClass().getSimpleName(), ListTools.getListFromArray(peroc), FrameType.percent_change, 0.050, -0.050);
		EncogSubframe esf6 = new EncogSubframe(getClass().getSimpleName(), ListTools.getListFromArray(perlh), FrameType.percent_change, 0.050, -0.050);
		
		encogFrame.addSubframe(esf1, esf2, esf3, esf4, esf5, esf6);
		
		return encogFrame;
	}
}
