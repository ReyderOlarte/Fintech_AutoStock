package com.autoStock.indicator.candleStick;

import com.autoStock.indicator.CommonAnalysisData;
import com.autoStock.indicator.IndicatorBase;
import com.autoStock.indicator.candleStick.CandleStickDefinitions.CandleStickIdentity;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.MInteger;
import com.autoStock.taLib.RetCode;

/**
 * @author Kevin Kowalewski
 *
 */
public class CandleStickIdentifier extends IndicatorBase {
	public CandleStickIdentifier(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType) {
		super(indicatorParameters, commonAnlaysisData, taLibCore, signalMetricType);
		this.taLibCore = taLibCore;
	}

	public CandleStickIdentifierResult identify(CandleStickIdentity candleStickIdentity){
		int [] arrayOfResults = new int[4];
		
		//cdlHangingMan 3
		//cdlAdvanceBlock 3
		//cdlDoji 4 //verbose
		//cdlDojiStar 4
		//cdlDragonflyDoji 4 //verbose
		//cdlEngulfing 4 //interesting 
		try {
			RetCode returnCode = taLibCore.cdlDojiStar(11, 14, arrayOfPriceOpen, arrayOfPriceHigh, arrayOfPriceLow, arrayOfPriceClose, new MInteger(), new MInteger(), arrayOfResults);
			handleAnalysisResult(returnCode);
		}catch (Exception e){}
		
		int i = 0;
		
//		Co.println("\n");
		
		for (int integer : arrayOfResults){
			if (integer != 0){
//				Co.println("--> RESULTS: " + arrayOfResults.length + ", " + i + " : " + integer);
//				throw new UnsupportedOperationException();
			}
			
			i++;
		}
		
		if (arrayOfResults[arrayOfResults.length-1] != 0){
//			throw new IllegalStateException();
		}
		
		return new CandleStickIdentifierResult(candleStickIdentity, arrayOfDates, arrayOfResults);
	}

	@Override
	public Object analyze() {
		return null; //pass
	}
}
