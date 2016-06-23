package com.autoStock.signal;

import java.util.ArrayList;
import java.util.Arrays;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.indicator.CommonAnalysisData;
import com.autoStock.indicator.IndicatorBase;
import com.autoStock.indicator.IndicatorGroup;
import com.autoStock.indicator.results.ResultsBase;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.autoStock.signal.SignalDefinitions.SignalParametersForADX;
import com.autoStock.signal.SignalDefinitions.SignalParametersForARDown;
import com.autoStock.signal.SignalDefinitions.SignalParametersForARUp;
import com.autoStock.signal.SignalDefinitions.SignalParametersForCCI;
import com.autoStock.signal.SignalDefinitions.SignalParametersForCandlestickGroup;
import com.autoStock.signal.SignalDefinitions.SignalParametersForCrossover;
import com.autoStock.signal.SignalDefinitions.SignalParametersForDI;
import com.autoStock.signal.SignalDefinitions.SignalParametersForEncog;
import com.autoStock.signal.SignalDefinitions.SignalParametersForMACD;
import com.autoStock.signal.SignalDefinitions.SignalParametersForMFI;
import com.autoStock.signal.SignalDefinitions.SignalParametersForROC;
import com.autoStock.signal.SignalDefinitions.SignalParametersForRSI;
import com.autoStock.signal.SignalDefinitions.SignalParametersForSAR;
import com.autoStock.signal.SignalDefinitions.SignalParametersForSTORSI;
import com.autoStock.signal.SignalDefinitions.SignalParametersForTRIX;
import com.autoStock.signal.SignalDefinitions.SignalParametersForUO;
import com.autoStock.signal.SignalDefinitions.SignalParametersForWILLR;
import com.autoStock.signal.SignalDefinitions.SignalParametersForPPC;
import com.autoStock.signal.extras.EncogFrame;
import com.autoStock.signal.signalMetrics.SignalOfADX;
import com.autoStock.signal.signalMetrics.SignalOfARDown;
import com.autoStock.signal.signalMetrics.SignalOfARUp;
import com.autoStock.signal.signalMetrics.SignalOfCCI;
import com.autoStock.signal.signalMetrics.SignalOfCandlestickGroup;
import com.autoStock.signal.signalMetrics.SignalOfCrossover;
import com.autoStock.signal.signalMetrics.SignalOfDI;
import com.autoStock.signal.signalMetrics.SignalOfEncog;
import com.autoStock.signal.signalMetrics.SignalOfEncogNew;
import com.autoStock.signal.signalMetrics.SignalOfMACD;
import com.autoStock.signal.signalMetrics.SignalOfMFI;
import com.autoStock.signal.signalMetrics.SignalOfPPC;
import com.autoStock.signal.signalMetrics.SignalOfROC;
import com.autoStock.signal.signalMetrics.SignalOfRSI;
import com.autoStock.signal.signalMetrics.SignalOfSAR;
import com.autoStock.signal.signalMetrics.SignalOfSTORSI;
import com.autoStock.signal.signalMetrics.SignalOfTRIX;
import com.autoStock.signal.signalMetrics.SignalOfUO;
import com.autoStock.signal.signalMetrics.SignalOfWILLR;
import com.autoStock.trading.types.Position;
import com.autoStock.types.basic.MutableInteger;

/**
 * @author Kevin Kowalewski
 *
 */
public class SignalGroup {
	public AlgorithmBase algorithmBase;
	public IndicatorGroup indicatorGroup;
	public SignalOfCCI signalOfCCI = new SignalOfCCI(new SignalParametersForCCI(), algorithmBase);
	public SignalOfADX signalOfADX = new SignalOfADX(new SignalParametersForADX(), algorithmBase);
	public SignalOfDI signalOfDI = new SignalOfDI(new SignalParametersForDI(), algorithmBase);
	public SignalOfMACD signalOfMACD = new SignalOfMACD(new SignalParametersForMACD(), algorithmBase);
	public SignalOfRSI signalOfRSI = new SignalOfRSI(new SignalParametersForRSI(), algorithmBase);
	public SignalOfTRIX signalOfTRIX = new SignalOfTRIX(new SignalParametersForTRIX(), algorithmBase);
	public SignalOfROC signalOfROC = new SignalOfROC(new SignalParametersForROC(), algorithmBase);
	public SignalOfSTORSI signalOfSTORSI = new SignalOfSTORSI(new SignalParametersForSTORSI(), algorithmBase);
	public SignalOfMFI signalOfMFI = new SignalOfMFI(new SignalParametersForMFI(), algorithmBase);
	public SignalOfWILLR signalOfWILLR = new SignalOfWILLR(new SignalParametersForWILLR(), algorithmBase);
	public SignalOfUO signalOfUO = new SignalOfUO(new SignalParametersForUO(), algorithmBase);
	public SignalOfARUp signalOfARUp = new SignalOfARUp(new SignalParametersForARUp(), algorithmBase);
	public SignalOfARDown signalOfARDown = new SignalOfARDown(new SignalParametersForARDown(), algorithmBase);
	public SignalOfSAR signalOfSAR = new SignalOfSAR(new SignalParametersForSAR(), algorithmBase);
	public SignalOfPPC signalOfPPC = new SignalOfPPC(new SignalParametersForPPC(), algorithmBase);
	
	public SignalOfCrossover signalOfCrossover = new SignalOfCrossover(SignalMetricType.metric_crossover, new SignalParametersForCrossover(), algorithmBase);
	
//	public SignalOfEncogNew signalOfEncog = new SignalOfEncogNew(new SignalParametersForEncog());
	public SignalOfEncog signalOfEncog = new SignalOfEncog(new SignalParametersForEncog(), algorithmBase);
	public SignalOfCandlestickGroup signalOfCandlestickGroup = new SignalOfCandlestickGroup(new SignalParametersForCandlestickGroup(), algorithmBase);
	
	private ArrayList<SignalBase> listOfSignalBase = new ArrayList<SignalBase>();
	private SignalGenerator signalGenerator = new SignalGenerator();
	
	public SignalGroup(AlgorithmBase algorithmBase){
		this.algorithmBase = algorithmBase; 
		
		listOfSignalBase.add(signalOfCCI);
		listOfSignalBase.add(signalOfADX);
		listOfSignalBase.add(signalOfDI);
		listOfSignalBase.add(signalOfMACD);
		listOfSignalBase.add(signalOfRSI);
		listOfSignalBase.add(signalOfTRIX);
		listOfSignalBase.add(signalOfROC);
		listOfSignalBase.add(signalOfSTORSI);
		listOfSignalBase.add(signalOfMFI);
		listOfSignalBase.add(signalOfWILLR);
		listOfSignalBase.add(signalOfUO);
		listOfSignalBase.add(signalOfARUp);
		listOfSignalBase.add(signalOfARDown);
		listOfSignalBase.add(signalOfSAR);
		listOfSignalBase.add(signalOfPPC);
		
		listOfSignalBase.add(signalOfCrossover);
		listOfSignalBase.add(signalOfEncog);
		
	}
	
	public void setIndicatorGroup(IndicatorGroup indicatorGroup){
		this.indicatorGroup = indicatorGroup;
		for (SignalBase signalBase : listOfSignalBase){
			signalBase.setTaLib(indicatorGroup.taLibCore);
			signalBase.algorithmBase = algorithmBase; //yeah...
		}
	}
	
	public void generateSignals(CommonAnalysisData commonAnalysisData, Position position){
		for (SignalBase signalBase : listOfSignalBase){
			if (signalBase instanceof SignalOfCandlestickGroup || signalBase instanceof SignalOfEncog){continue;}
			
			IndicatorBase<?> indicatorBase = indicatorGroup.getIndicatorByMetric(signalBase.signalMetricType);		
			signalBase.setCommonAnalysisData(commonAnalysisData);
			
			if (indicatorBase != null && indicatorBase.getResults() != null){
				signalBase.setInput((ResultsBase) indicatorBase.getResults());
			}else if (indicatorBase == null){
				signalBase.setInput(0);
			}
		}
	}
	
	public void processEncog(ArrayList<EncogFrame> encogFrames) {
		signalGenerator.generateEncogSignal(this, encogFrames);
	}
	
	public void processEncog(EncogFrame... encogFrames) {
		signalGenerator.generateEncogSignal(this, new ArrayList<EncogFrame>(Arrays.asList(encogFrames)));
	}
	
	public IndicatorGroup getIndicatorGroup(){
		return indicatorGroup;
	}

	public SignalBase getSignalBaseForType(SignalMetricType signalMetricType) {
		for (SignalBase signalBase : listOfSignalBase){
			if (signalBase != null && signalBase.signalMetricType == signalMetricType){
				return signalBase;
			}
		}
		
		return null;
//		throw new IllegalArgumentException("No SignalMetricType matched: " + signalMetricType.name() + ", " + listOfSignalBase.size());
	}
	
	public int getMaxPeriodLength() {
		int periodLength = 0;
		for (SignalBase signalBase : listOfSignalBase){
			if (signalBase.signalParameters.periodLength != null && signalBase.signalParameters.periodLength.value > 0){
				periodLength = Math.max(periodLength, signalBase.signalParameters.periodLength.value);
			}
		}
		
		return periodLength;
	}
	
	public ArrayList<SignalBase> getListOfSignalBase(){
		return listOfSignalBase;
	}
	
	public void reset(){
		for (SignalBase signalBase : listOfSignalBase){
			signalBase.reset();
		}
	}
}
