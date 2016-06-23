/**
 * 
 */
package com.autoStock.signal;

import java.util.ArrayList;
import java.util.Arrays;

import com.autoStock.Co;
import com.autoStock.signal.SignalDefinitions.SignalPointType;
import com.autoStock.signal.extras.EncogFrame;
import com.autoStock.signal.extras.EncogFrame.FrameType;
import com.autoStock.signal.extras.EncogInputWindow;
import com.autoStock.signal.extras.EncogSubframe;
import com.autoStock.signal.signalMetrics.SignalOfEncog;
import com.autoStock.tools.MathTools;

/**
 * @author Kevin
 *
 */
public class SignalGenerator {
	private static class UnableToProcessException extends Exception {}
	
	public void generateEncogSignal(SignalGroup signalGroup, ArrayList<EncogFrame> encogFrames){
		EncogInputWindow encogWindow = new EncogInputWindow();
		encogWindow.addFrames(encogFrames);
		try {encogWindow.addFrame(getFrameFromSignalValues(signalGroup));}catch(UnableToProcessException e){return;}
		encogWindow.addFrame(getFrameFromSignalPoints(signalGroup));
		encogWindow.addFrames(getFrameFromSignalEF(signalGroup));
		signalGroup.signalOfEncog.setInput(encogWindow);
	}
	
	private EncogFrame getFrameFromSignalValues(SignalGroup signalGroup) throws UnableToProcessException {
		FrameType frameType = FrameType.raw;
		
		EncogFrame encogFrame = new EncogFrame("Signals that output numeric values", frameType);
		SignalBase[] arrayOfSignalBase = {
//			signalGroup.signalOfCCI,
//			signalGroup.signalOfRSI,
//			signalGroup.signalOfUO,
//			signalGroup.signalOfTRIX,
//			signalGroup.signalOfWILLR,
			
//			signalGroup.signalOfDI,
//			signalGroup.signalOfPPC,
//			signalGroup.signalOfCrossover,
//			
			//DO NOT USE
//			signalGroup.signalOfMFI,
//			signalGroup.signalOfMACD,
//			signalGroup.signalOfROC,
//			signalGroup.signalOfARUp,
//			signalGroup.signalOfARDown,
//			signalGroup.signalOfADX,	

//			signalGroup.signalOfTRIX,
//			signalGroup.signalOfSAR,
		};
		
		if (signalGroup.signalOfEncog.isLongEnough(arrayOfSignalBase) == false){
			throw new UnableToProcessException();
		}
		
		for (SignalBase signalBase : arrayOfSignalBase){			
			EncogSubframe subframe = getSubFrame(signalBase, frameType);
			if (signalBase instanceof SignalBaseWithPoint){throw new IllegalArgumentException("SignalBase shouldn't be an instace of SignalBaseWithPoint");}
			if (subframe.isAllZeros() && subframe.asNormalizedDoubleList().size() > 5){Co.println(subframe.toString()); try {Thread.sleep(100);}catch(InterruptedException e){}; throw new IllegalStateException("Subframe for signal was all zeros: " + signalBase.getClass().getSimpleName());}
			if (subframe.isOutOfRange()){Co.println(subframe.toString()); try {Thread.sleep(100);}catch(InterruptedException e){}; throw new IllegalStateException("Subframe is out of range: " + subframe.description);}
			//subframe.replaceNaN(); //NaN discarded by encog
			encogFrame.addSubframe(subframe);			
		}
		
		return encogFrame;
	}
	
	private EncogFrame getFrameFromSignalPoints(SignalGroup signalGroup){
		EncogFrame encogFrame = new EncogFrame("Signals that output Signal Points", FrameType.category);
		
		SignalBase[] arrayOfSignalBase = {
//			signalGroup.signalOfCrossover
		};
		
		for (SignalBase signalBase : arrayOfSignalBase){
			if (signalBase instanceof SignalBaseWithPoint == false){throw new IllegalArgumentException("SignalBase should be an instance of SignalBaseWithPoint");}
			encogFrame.addSubframe(getSubFrameFromSignalPoint(signalBase));
		}
		
		return encogFrame;
	}
	
	private ArrayList<EncogFrame> getFrameFromSignalEF(SignalGroup signalGroup){
		ArrayList<EncogFrame> listOfFrame = new ArrayList<>();
		
		SignalBase[] arrayOfSignalBase = {
			signalGroup.signalOfPPC
		};
		
		for (SignalBase signalBase : arrayOfSignalBase){
			if (signalBase instanceof SignalBaseWithEF == false){throw new IllegalArgumentException("SignalBase should be an instance of SignalBaseWithEF");}
			listOfFrame.add(((SignalBaseWithEF)signalBase).asEncogFrame());
		}
		
		return listOfFrame;
	}
	
	public EncogSubframe getSubFrame(SignalBase signalBase, FrameType frameType){
		if (signalBase instanceof SignalBaseWithPoint){return getSubFrameFromSignalPoint(signalBase);}
		else {return getSubFrameFromValues(signalBase, frameType);}
	}
	
	public EncogSubframe getSubFrameFromSignalPoint(SignalBase signalBase){
		ArrayList<Double> values = new ArrayList<Double>();
		SignalPoint signalPoint = ((SignalBaseWithPoint)signalBase).getSignalPoint(signalBase.algorithmBase.position);
		if (signalPoint.signalPointType == SignalPointType.long_entry){values.add(1d);}else{values.add(-1d);}
		if (signalPoint.signalPointType == SignalPointType.short_entry){values.add(1d);}else{values.add(-1d);}
		if (signalPoint.signalPointType == SignalPointType.reentry){values.add(1d);}else{values.add(-1d);}
		if (signalPoint.signalPointType == SignalPointType.long_exit){values.add(1d);}else{values.add(-1d);}
		if (signalPoint.signalPointType == SignalPointType.short_exit){values.add(1d);}else{values.add(-1d);}
		return new EncogSubframe(signalBase.getClass().getSimpleName(), values, FrameType.category, 1, -1);
	}
	
	public EncogSubframe getSubFrameFromValues(SignalBase signalBase, FrameType frameType){
		EncogSubframe subFrame = null;
		
		if (frameType == FrameType.percent_change){
			subFrame = new EncogSubframe(signalBase.getClass().getSimpleName(), Arrays.copyOfRange(MathTools.getDeltasAsPercent(signalBase.getRawWindow(SignalOfEncog.INPUT_WINDOW_PS + 1)), 1, SignalOfEncog.INPUT_WINDOW_PS + 1), frameType, 1000, -1000);
		}if (frameType == FrameType.delta_change){
			subFrame = new EncogSubframe(signalBase.getClass().getSimpleName(), Arrays.copyOfRange(MathTools.getDeltas(signalBase.getNormalizedWindow(SignalOfEncog.INPUT_WINDOW_PS + 1)), 1, SignalOfEncog.INPUT_WINDOW_PS + 1), frameType, 50, -50);
		}else if (frameType == FrameType.raw){
			subFrame = new EncogSubframe(signalBase.getClass().getSimpleName(), Arrays.copyOfRange(signalBase.getNormalizedWindow(SignalOfEncog.INPUT_WINDOW_PS + 1), 1, SignalOfEncog.INPUT_WINDOW_PS + 1), frameType, 75, -75);
		}
		
		if (subFrame != null){return subFrame;}
		else {throw new IllegalStateException("Can't handle type: " + frameType.name());}
	}
}
