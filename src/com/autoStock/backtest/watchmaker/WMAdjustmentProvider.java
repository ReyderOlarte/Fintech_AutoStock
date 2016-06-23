package com.autoStock.backtest.watchmaker;

import java.util.ArrayList;

import com.autoStock.adjust.AdjustmentBase;
import com.autoStock.adjust.AdjustmentOfBasicBoolean;
import com.autoStock.adjust.AdjustmentOfBasicDouble;
import com.autoStock.adjust.AdjustmentOfBasicInteger;
import com.autoStock.adjust.IterableOfBoolean;
import com.autoStock.adjust.IterableOfDouble;
import com.autoStock.adjust.IterableOfInteger;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.indicator.IndicatorOfADX;
import com.autoStock.indicator.IndicatorOfCCI;
import com.autoStock.indicator.IndicatorOfDI;
import com.autoStock.indicator.IndicatorOfROC;
import com.autoStock.indicator.IndicatorOfUO;
import com.autoStock.indicator.IndicatorOfWILLR;
import com.autoStock.signal.signalMetrics.SignalOfEncog;

/**
 * @author Kevin Kowalewski
 *
 */
public class WMAdjustmentProvider {
	private WMAdjustmentGenerator wmAdjustmentGenerator =  new WMAdjustmentGenerator();
	
	public ArrayList<AdjustmentBase> getListOfAdjustmentBase(AlgorithmBase algorithmBase){
		ArrayList<AdjustmentBase> listOfAdjustmentBase = new ArrayList<AdjustmentBase>();
		
//		wmAdjustmentGenerator.addTypicalSignalRanges(algorithmBase.signalGroup.signalOfCCI,  listOfAdjustmentBase);
//		wmAdjustmentGenerator.addTypicalIndicatorParameters(algorithmBase.indicatorGroup.getIndicatorByClass(IndicatorOfCCI.class), listOfAdjustmentBase);
//		
//		wmAdjustmentGenerator.addTypicalSignalRanges(algorithmBase.signalGroup.signalOfDI,  listOfAdjustmentBase);
//		wmAdjustmentGenerator.addTypicalIndicatorParameters(algorithmBase.indicatorGroup.getIndicatorByClass(IndicatorOfDI.class), listOfAdjustmentBase);
//		
//		wmAdjustmentGenerator.addTypicalSignalRanges(algorithmBase.signalGroup.signalOfUO,  listOfAdjustmentBase);
//		wmAdjustmentGenerator.addTypicalIndicatorParameters(algorithmBase.indicatorGroup.getIndicatorByClass(IndicatorOfUO.class), listOfAdjustmentBase);
		
		listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("A", algorithmBase.signalGroup.signalOfCrossover.ipEMA1.periodLength, new IterableOfInteger(3, 15, 1)));
		listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("B", algorithmBase.signalGroup.signalOfCrossover.ipEMA2.periodLength, new IterableOfInteger(15, 45, 1)));
		wmAdjustmentGenerator.addTypicalSignalRanges(algorithmBase.signalGroup.signalOfCrossover, listOfAdjustmentBase);
		
		
//		listOfAdjustmentBase.addAll(wmAdjustmentGenerator.getTypicalAdjustmentForIndicator(algorithmBase.indicatorGroup.getIndicatorByClass(IndicatorOfCCI.class)));
//		listOfAdjustmentBase.addAll(wmAdjustmentGenerator.getTypicalAdjustmentForIndicator(algorithmBase.indicatorGroup.getIndicatorByClass(IndicatorOfUO.class)));
//		listOfAdjustmentBase.addAll(wmAdjustmentGenerator.getTypicalAdjustmentForIndicator(algorithmBase.indicatorGroup.getIndicatorByClass(IndicatorOfDI.class)));
//		wmAdjustmentGenerator.addCustomIndicatorParameters(algorithmBase.indicatorGroup.getIndicatorByClass(IndicatorOfADX.class), listOfAdjustmentBase, 20, 60);
//		wmAdjustmentGenerator.addCustomIndicatorParameters(algorithmBase.indicatorGroup.getIndicatorByClass(IndicatorOfWILLR.class), listOfAdjustmentBase, 20, 60);
//		wmAdjustmentGenerator.addCustomIndicatorParameters(algorithmBase.indicatorGroup.getIndicatorByClass(IndicatorOfROC.class), listOfAdjustmentBase, 20, 60);
//		
//		wmAdjustmentGenerator.addSignalAverage(listOfAdjustmentBase, algorithmBase.signalGroup.signalOfCCI);
//		wmAdjustmentGenerator.addSignalAverage(listOfAdjustmentBase, algorithmBase.signalGroup.signalOfUO);
//		wmAdjustmentGenerator.addSignalAverage(listOfAdjustmentBase, algorithmBase.signalGroup.signalOfDI);
//		wmAdjustmentGenerator.addSignalAverage(listOfAdjustmentBase, algorithmBase.signalGroup.signalOfADX);
//		wmAdjustmentGenerator.addSignalAverage(listOfAdjustmentBase, algorithmBase.signalGroup.signalOfWILLR);
//		wmAdjustmentGenerator.addSignalAverage(listOfAdjustmentBase, algorithmBase.signalGroup.signalOfROC);
		
		
//		listOfAdjustmentBase.add(new AdjustmentOfEnum<SignalPointTactic>("SO Tactic Entry", new IterableOfEnum<SignalPointTactic>(SignalPointTactic.tactic_any, SignalPointTactic.tactic_combined), algorithmBase.strategyBase.strategyOptions.signalPointTacticForEntry));
//		listOfAdjustmentBase.add(new AdjustmentOfEnum<SignalPointTactic>("SO Tactic Exit", new IterableOfEnum<SignalPointTactic>(SignalPointTactic.tactic_any, SignalPointTactic.tactic_combined), algorithmBase.strategyBase.strategyOptions.signalPointTacticForExit));
		
		//Long, Short & Reentry
//		listOfAdjustmentBase.add(new AdjustmentOfBasicBoolean("SO canGoLong", algorithmBase.strategyBase.strategyOptions.canGoLong, new IterableOfBoolean()));
//		listOfAdjustmentBase.add(new AdjustmentOfBasicBoolean("SO canGoShort", algorithmBase.strategyBase.strategyOptions.canGoShort, new IterableOfBoolean()));
//		listOfAdjustmentBase.add(new AdjustmentOfBasicBoolean("SO canReenter", algorithmBase.strategyBase.strategyOptions.canReenter, new IterableOfBoolean()));
		
		//Stop Loss & Profit Drawdown
//		listOfAdjustmentBase.add(new AdjustmentOfBasicDouble("SO maxStopLossPercent", algorithmBase.strategyBase.strategyOptions.maxStopLossPercent, new IterableOfDouble(-0.25, 0, 0.01)));
//     	listOfAdjustmentBase.add(new AdjustmentOfBasicDouble("SO maxProfitDrawdownPercent", algorithmBase.strategyBase.strategyOptions.maxProfitDrawdownPercent, new IterableOfDouble(-0.25, 0, 0.01)));
//     	listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("SO maxTimeAtProfit", algorithmBase.strategyBase.strategyOptions.maxPositionTimeAtProfit, new IterableOfInteger(0, 45, 1)));
//     	listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("SO maxTimeAtLoss", algorithmBase.strategyBase.strategyOptions.maxPositionTimeAtProfit, new IterableOfInteger(0, 45, 1)));
//     	listOfAdjustmentBase.add(new AdjustmentOfBasicDouble("SO minTimeAtProfitYield", algorithmBase.strategyBase.strategyOptions.minPositionTimeAtProfitYield, new IterableOfDouble(0, 1.0, 0.10)));
     	
     	//Reentry
//     	listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("SO intervalForReentryMins", algorithmBase.strategyBase.strategyOptions.intervalForReentryMins, new IterableOfInteger(1, 15, 1)));
//     	listOfAdjustmentBase.add(new AdjustmentOfBasicDouble("SO minReentryPercentGain", algorithmBase.strategyBase.strategyOptions.minReentryPercentGain, new IterableOfDouble(0, 0.50, 0.01)));
//     	listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("SO maxReenterTimes", algorithmBase.strategyBase.strategyOptions.maxReenterTimesPerPosition, new IterableOfInteger(1, 5, 1)));

     	//Intervals
     	//listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("SO entryAfterStopLossMinutes", algorithmBase.strategyBase.strategyOptions.intervalForEntryAfterExitWithLossMins, new IterableOfInteger(1, 20, 1)));
     	//listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("SO intervalForEntryWithSameSignalPointType", algorithmBase.strategyBase.strategyOptions.intervalForEntryWithSameSignalPointType, new IterableOfInteger(1, 20, 1)));
     	
     	//Misc
//     	listOfAdjustmentBase.add(new AdjustmentOfBasicInteger("SO maxTransactionsPerDay", algorithmBase.strategyBase.strategyOptions.maxTransactionsDay, new IterableOfInteger(3, 32, 1)));
//     	listOfAdjustmentBase.add(new AdjustmentOfBasicDouble("SO disableAfterYield", algorithmBase.strategyBase.strategyOptions.disableAfterYield, new IterableOfDouble(0, 3.00, 0.10)));
//     	listOfAdjustmentBase.add(new AdjustmentOfBasicDouble("SO disableAfterLoss", algorithmBase.strategyBase.strategyOptions.disableAfterLoss, new IterableOfDouble(-3.00, 0, 0.10)));
     	
		return listOfAdjustmentBase;
	}
}

