package com.autoStock.indicator;

import java.util.ArrayList;

import com.autoStock.Co;
import com.autoStock.indicator.candleStick.CandleStickIdentifier;
import com.autoStock.indicator.candleStick.CandleStickIdentifierResult;
import com.autoStock.indicator.results.ResultsEMA;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForADX;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForARUp;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForBasic;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForCCI;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForDI;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForMACD;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForMFI;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForROC;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForRSI;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForSAR;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForSTORSI;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForTRIX;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForUO;
import com.autoStock.signal.SignalDefinitions.IndicatorParametersForWILLR;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalGroup;
import com.autoStock.taLib.Core;
import com.autoStock.tools.Benchmark;

/**
 * @author Kevin Kowalewski
 * 
 */
public class IndicatorGroup {
	public Core taLibCore = new Core();
	
	public CandleStickIdentifier candleStickIdentifier;
	
	public ResultsEMA resultsEMAFirst;
	public ResultsEMA resultsEMASecond;
	
	public CandleStickIdentifierResult candleStickIdentifierResult;
	
	public SignalGroup signalGroup;
	public CommonAnalysisData commonAnalysisData;

	private ArrayList<IndicatorBase> listOfIndicatorBase = new ArrayList<IndicatorBase>();
	private ArrayList<SignalMetricType> listOfSignalMetricTypeAnalyze = new ArrayList<SignalMetricType>();
	private ArrayList<SignalMetricType> listOfSignalMetricTypeActive = new ArrayList<SignalMetricType>();
	private Benchmark bench = new Benchmark();

	public IndicatorGroup(CommonAnalysisData commonAnlaysisData, SignalGroup signalGroup) {
		this.signalGroup = signalGroup;
		this.commonAnalysisData = commonAnlaysisData;
		
		listOfIndicatorBase.clear();
		listOfIndicatorBase.add(new IndicatorOfADX(new IndicatorParametersForADX(), commonAnalysisData, taLibCore, SignalMetricType.metric_adx));
		listOfIndicatorBase.add(new IndicatorOfCCI(new IndicatorParametersForCCI(), commonAnalysisData, taLibCore, SignalMetricType.metric_cci));
		listOfIndicatorBase.add(new IndicatorOfDI(new IndicatorParametersForDI(), commonAnalysisData, taLibCore, SignalMetricType.metric_di));
		listOfIndicatorBase.add(new IndicatorOfMACD(new IndicatorParametersForMACD(), commonAnalysisData, taLibCore, SignalMetricType.metric_macd));
		listOfIndicatorBase.add(new IndicatorOfRSI(new IndicatorParametersForRSI(), commonAnalysisData, taLibCore, SignalMetricType.metric_rsi));
		listOfIndicatorBase.add(new IndicatorOfTRIX(new IndicatorParametersForTRIX(), commonAnalysisData, taLibCore, SignalMetricType.metric_trix));
		listOfIndicatorBase.add(new IndicatorOfROC(new IndicatorParametersForROC(), commonAnalysisData, taLibCore, SignalMetricType.metric_roc));
		listOfIndicatorBase.add(new IndicatorOfMFI(new IndicatorParametersForMFI(), commonAnalysisData, taLibCore, SignalMetricType.metric_mfi));
		listOfIndicatorBase.add(new IndicatorOfSTORSI(new IndicatorParametersForSTORSI(), commonAnalysisData, taLibCore, SignalMetricType.metric_storsi));
		listOfIndicatorBase.add(new IndicatorOfWILLR(new IndicatorParametersForWILLR(), commonAnalysisData, taLibCore, SignalMetricType.metric_willr));
		listOfIndicatorBase.add(new IndicatorOfUO(new IndicatorParametersForUO(), commonAnalysisData, taLibCore, SignalMetricType.metric_uo));
		listOfIndicatorBase.add(new IndicatorOfAR(new IndicatorParametersForARUp(), commonAnalysisData, taLibCore, SignalMetricType.metric_ar_up));
		listOfIndicatorBase.add(new IndicatorOfSAR(new IndicatorParametersForSAR(), commonAnalysisData, taLibCore, SignalMetricType.metric_sar));
		listOfIndicatorBase.add(new IndicatorOfCSO(new IndicatorParametersForBasic(), commonAnlaysisData, taLibCore, SignalMetricType.none));
//		listOfIndicatorBase.add(new IndicatorOfBB(new MutableInteger(0), 1, commonAnalysisData, taLibCore, SignalMetricType.none));
		
		//listOfIndicatorBase.add(new IndicatorOfEMA(new IndicatorParametersForEMAFirst(), commonAnalysisData, taLibCore, SignalMetricType.metric_crossover));
		//listOfIndicatorBase.add(new IndicatorOfEMA(new IndicatorParametersForEMASecond(), commonAnalysisData, taLibCore, SignalMetricType.metric_crossover));
		
//		listOfIndicatorBase.add(candleStickIdentifier = new CandleStickIdentifier(new IndicatorParameters(new MutableInteger(30), 1) {}, commonAnalysisData, taLibCore, SignalMetricType.metric_candlestick_group));
	}
	
	@SuppressWarnings("rawtypes")
	public IndicatorBase getIndicatorByClass(Class clazz){
		for (IndicatorBase indicator : listOfIndicatorBase){
			if (clazz.isInstance(indicator)){
				return indicator;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public IndicatorBase getIndicatorByMetric(SignalMetricType metricType){
		for (IndicatorBase indicator : listOfIndicatorBase){
			if (indicator.getSignalMetricType() == metricType){
				return indicator;
			}
		}
		
		return null;
	}
	
	public void setDataSet(){
		for (IndicatorBase<?> indicator : listOfIndicatorBase){
			for (SignalMetricType signalMetricType : indicator.getSignalMetricTypeList()){
				if (listOfSignalMetricTypeAnalyze.contains(signalMetricType)){
					indicator.setDataSet();
				}
			}
		}
		
		if (candleStickIdentifier != null){candleStickIdentifier.setDataSet();} 
	}
	
	public void setAnalyze(ArrayList<SignalMetricType> listOfSignalMetricType) {
		if (listOfSignalMetricType == null){throw new NullPointerException();}
		this.listOfSignalMetricTypeAnalyze = (ArrayList<SignalMetricType>) listOfSignalMetricType.clone();
	}
	
	public void setActive(ArrayList<SignalMetricType> listOfSignalMetricType) {
		if (listOfSignalMetricType == null){throw new NullPointerException();}
		this.listOfSignalMetricTypeActive = (ArrayList<SignalMetricType>) listOfSignalMetricType.clone();
	}
	
	public void analyze(){
		for (IndicatorBase indicatorBase : listOfIndicatorBase){
			if (listOfSignalMetricTypeAnalyze.contains(indicatorBase.getSignalMetricType())){
				indicatorBase.analyze();
			}
		}
	}
	
	public ArrayList<IndicatorBase> getListOfIndicatorBaseActive(){
		ArrayList<IndicatorBase> list = new ArrayList<IndicatorBase>();
		
		for (IndicatorBase<?> indicatorBase : listOfIndicatorBase){
			for (SignalMetricType signalMetricType : indicatorBase.getSignalMetricTypeList()){
				if (listOfSignalMetricTypeActive.contains(signalMetricType)){
					list.add(indicatorBase);
				}
			}
		}
		
		return list;
	}
	
	public ArrayList<IndicatorBase> getListOfIndicatorBase(){
		return listOfIndicatorBase;
	}

	public int getMinPeriodLength(boolean includeAll) {
		int min = 0;
		IndicatorBase<?> indcatorBase = null;
		
		for (IndicatorBase<?> indicator : listOfIndicatorBase){
			if (indicator instanceof CandleStickIdentifier == false){
				for (SignalMetricType signalMetricType : indicator.getSignalMetricTypeList()){
//					Co.println("--> Indicator, length: " + indicator.getClass().getSimpleName() + ", " + indicator.getRequiredDatasetLength());
					if (listOfSignalMetricTypeAnalyze.contains(signalMetricType) || includeAll){
						if (indicator.getRequiredDatasetLength() > min){
							min = indicator.getRequiredDatasetLength();
							indcatorBase = indicator;
						}
					}
				}
			}
		}
		
//		Co.println("--> Longest required period length from: " + indcatorBase.getClass().getSimpleName() + ", with: " + min);
		return min;
	}
}
