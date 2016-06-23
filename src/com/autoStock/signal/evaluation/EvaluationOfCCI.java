package com.autoStock.signal.evaluation;

import java.util.Arrays;

import com.autoStock.signal.SignalPoint;
import com.autoStock.tools.ArrayTools;
import com.autoStock.tools.MathTools;

/**
 * @author Kevin Kowalewski
 *
 */
public class EvaluationOfCCI extends EvaulationBase {
	private DetectorTools detectorTools = new DetectorTools();
	private int[] arrayOfNormalizedCCI;
	private int detectWindow = 10;
	
	public EvaluationOfCCI(int[] arrayOfNormalizedCCI){
		this.arrayOfNormalizedCCI =  arrayOfNormalizedCCI;
		
		if (this.arrayOfNormalizedCCI.length >= detectWindow){
			this.arrayOfNormalizedCCI = Arrays.copyOfRange(this.arrayOfNormalizedCCI, arrayOfNormalizedCCI.length-detectWindow, arrayOfNormalizedCCI.length);
		}
	}

	@Override
	public SignalPoint getSignalPoint() {
		if (arrayOfNormalizedCCI.length == detectWindow){
			int cciValue = arrayOfNormalizedCCI[arrayOfNormalizedCCI.length-1];
			int changeFromPeak = detectorTools.getChangeFromPeak(arrayOfNormalizedCCI) * -1;
			int changeFromTrough = detectorTools.getChangeFromTrough(arrayOfNormalizedCCI);
			int changeFromMaxToMin = detectorTools.getChangeBetweenMaxAndMin(arrayOfNormalizedCCI);
			
			int peakValue = MathTools.getMax(arrayOfNormalizedCCI);
			int peakIndex = ArrayTools.getLastIndex(arrayOfNormalizedCCI, peakValue);
			int troughValue = MathTools.getMin(arrayOfNormalizedCCI);
			int troughIndex = ArrayTools.getLastIndex(arrayOfNormalizedCCI, troughValue);
			
			boolean directionSincePeakIsDown = detectorTools.directionIsDown(Arrays.copyOfRange(arrayOfNormalizedCCI, peakIndex, arrayOfNormalizedCCI.length), 2);
			boolean directionSincePeakIsUp = detectorTools.directionIsUp(Arrays.copyOfRange(arrayOfNormalizedCCI, peakIndex, arrayOfNormalizedCCI.length), 2);
			boolean directionSinceTroughIsDown = detectorTools.directionIsDown(Arrays.copyOfRange(arrayOfNormalizedCCI, troughIndex, arrayOfNormalizedCCI.length), 2);
			boolean directionSinceTroughIsUp = detectorTools.directionIsUp(Arrays.copyOfRange(arrayOfNormalizedCCI, troughIndex, arrayOfNormalizedCCI.length), 2);
			
			boolean hasTroughed = changeFromTrough >= 8 && directionSinceTroughIsUp && troughIndex > 0;
			boolean hasPeaked = changeFromPeak >= 8 && directionSincePeakIsDown && peakIndex > 0;
			
//			Co.println("--> Change: " + changeFromTrough + ", " + changeFromPeak + ", " + peakIndex + ", " + peakValue);
			
//			if (cciValue > SignalMetricType.metric_cci.pointToSignalLongExit){
//				return new SignalPoint(SignalPointType.long_exit, SignalMetricType.metric_cci);
//			}else if (cciValue < SignalMetricType.metric_cci.pointToSignalLongEntry){
//				return new SignalPoint(SignalPointType.long_entry, SignalMetricType.metric_cci);
//			}
			
//			if (hasTroughed && cciValue <= -20){
////				Co.println("--> ********** ********** ENTRY");
//				return new SignalPoint(SignalPointType.long_entry, SignalMetricType.metric_cci);
//			}else if (hasPeaked && cciValue >= 20){
////				Co.println("--> ********** EXIT");
//				return new SignalPoint(SignalPointType.long_exit, SignalMetricType.metric_cci);
//			}
		}

		return new SignalPoint();
	}

}
