package com.autoStock.signal;

import java.util.ArrayList;

import com.autoStock.Co;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalPointType;
import com.autoStock.tools.MathTools;
import com.autoStock.trading.types.Position;

/**
 * @author Kevin Kowalewski
 *
 */
public class TacticResolver {
	public static enum SignalPointTactic {
		tactic_majority,
		tactic_any,
		tactic_combined,
		tactic_mixed,
	}
	
	public static SignalPoint getSignalPoint(Signaler signal, SignalPointTactic signalPointTactic, Position position){ //synchronized?
		SignalPoint signalPoint;
		
		if (signalPointTactic == SignalPointTactic.tactic_any){
			signalPoint = getSignalPointChange(signal, position);
		}else if (signalPointTactic == SignalPointTactic.tactic_majority){
			signalPoint = getSignalPointMajority(signal, position);
		}else if (signalPointTactic == SignalPointTactic.tactic_combined){
			signalPoint = getSignalPointCombined(signal, position);
		}else if (signalPointTactic == SignalPointTactic.tactic_mixed){
			return getSignalPointMixed(signal, position);
		}else{
			throw new UnsupportedOperationException();
		}
		
		return signalPoint;
	}
	
	private static SignalPoint getSignalPointMixed(Signaler signal, Position position){
		SignalPoint signalPoint = new SignalPoint();
		
		SignalPoint signalPointFromCCI = signal.getSignalGroup().getSignalBaseForType(SignalMetricType.metric_cci).getSignalPoint(position);
		SignalPoint signalPointFromDI = signal.getSignalGroup().getSignalBaseForType(SignalMetricType.metric_di).getSignalPoint(position);
		
		signalPoint = signalPointFromCCI;
		
		return signalPoint;
	}
	
	private static SignalPoint getSignalPointCombined(Signaler signal, Position position){
		SignalPoint signalPoint = new SignalPoint();
		
		//Co.println("--> Using combined " + signal.getListOfSignalBase().size());
		
		for (SignalBase signalBase : signal.getListOfSignalBase()){
			SignalPoint signalPointLocal = signalBase.getSignalPoint(position);
				
			if (signalPointLocal.signalPointType == SignalPointType.none){
				signalPoint = new SignalPoint();
				break;
			}else if (signalPoint.signalPointType == SignalPointType.none){
				signalPoint = signalPointLocal;
			}else{
				if (signalPointLocal.signalPointType != signalPoint.signalPointType){
					signalPoint = new SignalPoint();
					break;
				}
			}
		}
		
		//Co.println("--> SignalPoint, type: " + signalPoint.signalPointType.name() + ", " + signalPoint.signalMetricType);
		
		return signalPoint;
	}
	
	private static SignalPoint getSignalPointMajority(Signaler signal, Position position){
		SignalPoint signalPoint = new SignalPoint();
		int occurenceCount = 0;
		boolean isEvenNumberOfMetrics = MathTools.isEven(signal.getListOfSignalBase().size());
		
		ArrayList<SignalPointPair> listOfSignalPointPair = new ArrayList<SignalPointPair>();
		
		for (SignalBase signalBase : signal.getListOfSignalBase()){
			SignalPoint signalPointLocal = signalBase.getSignalPoint(position);
			SignalPointPair signalPointPair = getPairForType(signalPointLocal.signalPointType, listOfSignalPointPair);
			
			if (signalPointPair == null){
				listOfSignalPointPair.add(new SignalPointPair(signalPointLocal.signalPointType));
			}else{
				signalPointPair.occurences++;
			}
		}
		
		for (SignalPointPair signalPointPair : listOfSignalPointPair){
			if (signalPointPair.occurences > occurenceCount || (isEvenNumberOfMetrics && signalPointPair.signalPointType != SignalPointType.none)){
				signalPoint = new SignalPoint(signalPointPair.signalPointType, SignalMetricType.mixed);
				occurenceCount = signalPointPair.occurences;
			}
		}
		
//		Co.println("--> Majority is: " + signalPoint.signalPointType.name() + ", " + occurenceCount);
		return signalPoint;
	} 
	
	private static SignalPoint getSignalPointChange(Signaler signal, Position position){
		SignalPoint signalPoint = new SignalPoint();
		
		for (SignalBase signalBase : signal.getListOfSignalBase()){
			SignalPoint metricSignalPoint = signalBase.getSignalPoint(position);
			if (metricSignalPoint.signalPointType != SignalPointType.none){
				signalPoint = metricSignalPoint;
				signalPoint.signalMetricType = signalBase.signalMetricType;
				return signalPoint;
			}
		}
		
//		Co.println("--> Change is: " + signalPoint.name() + ", " + signalPoint.signalMetricType.name());
		
		return signalPoint;
	}
	
	private static SignalPointPair getPairForType(SignalPointType signalPointType, ArrayList<SignalPointPair> listOfSignalPointPair){
		for (SignalPointPair signalPointPair : listOfSignalPointPair){
			if (signalPointPair.signalPointType == signalPointType){
				return signalPointPair;
			}
		}
		
		return null;
	}
	
	public static class SignalPointPair {
		public int occurences = 1;
		public SignalPointType signalPointType;
		
		public SignalPointPair(SignalPointType signalPointType){
			this.signalPointType = signalPointType;
		}
	}
}
