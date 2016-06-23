/**
 * 
 */
package com.autoStock.signal;

import java.util.ArrayList;

import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalSource;
import com.autoStock.signal.signalMetrics.SignalOfEncog;
import com.autoStock.trading.types.Position;

/**
 * @author Kevin Kowalewski
 *
 */
public class Signaler {
	private SignalGroup signalGroup;
	public SignalSource signalSource;
	public SignalPoint currentSignalPoint = new SignalPoint();
	private ArrayList<SignalBase> listOfSignalBase = new ArrayList<SignalBase>();
	private ArrayList<SignalMoment> listOfSignalMoment = new ArrayList<SignalMoment>();
	
	public Signaler(SignalSource signalSource, SignalGroup signalGroup) {
		this.signalSource = signalSource;
		this.signalGroup = signalGroup;
	}
	
	public void addSignalBaseFromMetrics(ArrayList<SignalMetricType> listOfSignalMetricType){
		for (SignalMetricType signalMetricType : listOfSignalMetricType){
			listOfSignalBase.add(signalGroup.getSignalBaseForType(signalMetricType));
		}
	}
	
	public void generateSignalMoments(Position position){
		for (SignalBase signalBase : listOfSignalBase){
			if (signalBase instanceof SignalOfEncog == false){
				listOfSignalMoment.add(new SignalMoment(signalBase.signalMetricType, signalBase.getStrength(), signalBase.getSignalPoint(position), null));
			}else{
				listOfSignalMoment.add(new SignalMoment(signalBase.signalMetricType, 0, new SignalPoint(), null));
			}
		}
	}
	
	public ArrayList<SignalBase> getListOfSignalBase(){
		return listOfSignalBase;
	}
	
	public SignalGroup getSignalGroup(){
		return signalGroup;
	}

	public ArrayList<SignalMoment> getListOfSignalMoment() {
		return listOfSignalMoment;
	}
}
