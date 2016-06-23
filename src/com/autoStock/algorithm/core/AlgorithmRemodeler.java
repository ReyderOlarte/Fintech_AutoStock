package com.autoStock.algorithm.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.backtest.AlgorithmModel;
import com.autoStock.indicator.IndicatorBase;
import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalBase.SignalExtra;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;

/**
 * @author Kevin Kowalewski
 *
 */
public class AlgorithmRemodeler {
	private AlgorithmBase algorithmBase;
	private AlgorithmModel algorithmModel;
	
	public AlgorithmRemodeler(AlgorithmBase algorithmBase, AlgorithmModel algorithmModel) {
		this.algorithmBase = algorithmBase;
		this.algorithmModel = algorithmModel;
	}
	
	public void remodel(){
		remodel(true, true, true, true);
	}

	public void remodel(boolean includeStrategyOptions, boolean includeSignalParameters, boolean includeIndicatorParameters, boolean includeSignalMetricList){
		Object object = null;
		
		if (includeSignalMetricList == false){
			object = algorithmBase.strategyBase.strategyOptions.listOfSignalMetricType.clone();
		}
		
		if (includeStrategyOptions){
			algorithmBase.strategyBase.strategyOptions = algorithmModel.strategyOptions;
		}else{
			Co.println("--> Warning: Remodeler discaring strategy options");
		}
		
		if (includeSignalMetricList ==  false){
			algorithmBase.strategyBase.strategyOptions.listOfSignalMetricType = (ArrayList<SignalMetricType>) object;
		}
		
		if (includeSignalParameters){
			setSignalBaseParamaters(algorithmBase.signalGroup.getListOfSignalBase(), algorithmModel.listOfSignalParameters);
		}else{
			Co.println("--> Warning: Remodeler discarding signal parameters");
		}
		
		if (includeIndicatorParameters){
			setIndicatorBaseParameters(algorithmBase.indicatorGroup.getListOfIndicatorBase(), algorithmModel.listOfIndicatorParameters);
		}else{
			Co.println("--> Warning: Remodeler discarding indicator parameters");
		}
	}
	
	public void setSignalBaseParamaters(List<SignalBase> listOfSignalBase, List<SignalParameters> listOfSignalParameters){
		for (SignalBase signalBase : listOfSignalBase){
			for (SignalParameters signalParameters : listOfSignalParameters){
				if (signalBase.signalParameters.getClass() == signalParameters.getClass()){
					signalBase.signalParameters = signalParameters;
					if (signalBase instanceof SignalExtra){
						((SignalExtra)signalBase).fromExtra(new String(Base64.decodeBase64(signalBase.signalParameters.extras)));
					}
				}
			}
		}
	}
	
	public void setIndicatorBaseParameters(List<IndicatorBase> listOfIndicatorBase, List<IndicatorParameters> listOfIndicatorParameters){
		for (IndicatorBase indicatorBase : listOfIndicatorBase){
			for (IndicatorParameters indicatorParameters : listOfIndicatorParameters){
				if (indicatorBase.indicatorParameters.getClass() == indicatorParameters.getClass()){
					indicatorBase.indicatorParameters = indicatorParameters;
				}
			}
		}
	}
}
