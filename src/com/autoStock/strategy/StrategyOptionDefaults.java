package com.autoStock.strategy;

import com.autoStock.algorithm.extras.StrategyOptionsOverride;
import com.autoStock.signal.TacticResolver.SignalPointTactic;

/**
 * @author Kevin Kowalewski
 *
 */
public class StrategyOptionDefaults {
	
	public static StrategyOptionsOverride getDefaultOverride() {
		return new StrategyOptionsOverride() {
			@Override
			public void override(StrategyOptions strategyOptions) {
				strategyOptions.intervalForEntryWithSameSignalPointType.value = 7;
				strategyOptions.minPositionAgeMinsBeforeExit.value = 3;
				strategyOptions.invervalForDDorSLExitMins.value = 3;
				
				strategyOptions.maxProfitDrawdownPercent.value = -0.25d;
				strategyOptions.maxStopLossPercent.value = -0.25d;
				strategyOptions.disableAfterLoss.value = -0.50d;
				
				strategyOptions.maxPositionTimeAtProfit.value = 30;
				strategyOptions.disableAfterYield.value = 10.0d;
				strategyOptions.enablePremise = false;
				strategyOptions.enableContext = false;
				strategyOptions.enablePrefill = true;
				strategyOptions.canReenter.value = false;
				strategyOptions.prefillShift.value = 30;
				strategyOptions.maxTransactionsDay.value = 999;
				
				//strategyOptions.maxProfitDrawdownPercent.value = -99d;
				//strategyOptions.maxStopLossPercent.value = -99d;
				//strategyOptions.disableAfterLoss.value = -99d;
				
				strategyOptions.signalPointTacticForEntry.value = SignalPointTactic.tactic_combined;
				strategyOptions.signalPointTacticForExit.value = SignalPointTactic.tactic_combined;
			}
		};
	}
	
	public static StrategyOptions getDefaultStrategyOptions(){
		StrategyOptions strategyOptions = new StrategyOptions();
		strategyOptions.canGoLong.value = true;
		strategyOptions.canGoShort.value = true;
		strategyOptions.canReenter.value = false;
		strategyOptions.mustHavePositiveSlice = false;
		strategyOptions.disableAfterNilChanges = true;
		strategyOptions.disableAfterNilVolumes = true;
		strategyOptions.enablePrefill = true;
		strategyOptions.enablePremise = false;
		strategyOptions.enableContext = false;
		strategyOptions.signalPointTacticForEntry.value = SignalPointTactic.tactic_any;
		strategyOptions.signalPointTacticForExit.value = SignalPointTactic.tactic_any;
		
		strategyOptions.maxTransactionsDay.value = 16;
		strategyOptions.maxStopLossPercent.value = -0.10d;
		strategyOptions.maxProfitDrawdownPercent.value = -0.15d;
		strategyOptions.invervalForDDorSLExitMins.value = 5;
		strategyOptions.maxNilChangePrice = 15;
		strategyOptions.maxNilChangeVolume = 15;
		strategyOptions.maxPositionEntryTime = 15;
		strategyOptions.maxPositionExitTime = 5;
		strategyOptions.maxPositionTimeAtLoss.value = 60;
		strategyOptions.maxPositionTimeAtProfit.value = 45;
		strategyOptions.minPositionTimeAtProfitYield.value = 0; //.25d;
		strategyOptions.maxReenterTimesPerPosition.value = 3;
		strategyOptions.intervalForReentryMins.value = 2;
		strategyOptions.minReentryPercentGain.value = 0.15;
		strategyOptions.prefillShift.value = 0;
		strategyOptions.minPositionAgeMinsBeforeExit.value = 5;
		strategyOptions.intervalForEntryAfterExitWithLossMins.value = 5;
		strategyOptions.intervalForEntryWithSameSignalPointType.value = 0;
		strategyOptions.disableAfterLoss.value = -0.25d;
		strategyOptions.disableAfterYield.value = 2.00d;
		
		return strategyOptions;
	}
}
