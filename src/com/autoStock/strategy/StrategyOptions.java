package com.autoStock.strategy;

import java.util.ArrayList;

import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.TacticResolver.SignalPointTactic;
import com.autoStock.types.basic.MutableBoolean;
import com.autoStock.types.basic.MutableDouble;
import com.autoStock.types.basic.MutableEnum;
import com.autoStock.types.basic.MutableInteger;
import com.rits.cloning.Cloner;

/**
 * @author Kevin Kowalewski
 *
 */
public class StrategyOptions implements Cloneable {
	public MutableBoolean canGoLong = new MutableBoolean(true);
	public MutableBoolean canGoShort = new MutableBoolean(true);
	public MutableBoolean canReenter = new MutableBoolean(true);
	public boolean disableAfterNilChanges;
	public boolean disableAfterNilVolumes;
	public boolean mustHavePositiveSlice;
	public MutableEnum<SignalPointTactic> signalPointTacticForEntry = new MutableEnum<SignalPointTactic>();
	public MutableEnum<SignalPointTactic> signalPointTacticForExit = new MutableEnum<SignalPointTactic>();
	
	public MutableInteger maxTransactionsDay = new MutableInteger();
	public MutableDouble minReentryPercentGain = new MutableDouble();
	public MutableDouble maxStopLossPercent = new MutableDouble();
	public MutableDouble maxProfitDrawdownPercent = new MutableDouble();
	public MutableInteger invervalForDDorSLExitMins = new MutableInteger();
	public int maxNilChangePrice;
	public int maxNilChangeVolume;
	public int maxPositionEntryTime;
	public int maxPositionExitTime;
	public MutableInteger maxReenterTimesPerPosition = new MutableInteger();
	public MutableInteger intervalForReentryMins = new MutableInteger();
	public MutableInteger minPositionAgeMinsBeforeExit = new MutableInteger();
	public MutableInteger intervalForEntryAfterExitWithLossMins = new MutableInteger();
	public MutableInteger intervalForEntryWithSameSignalPointType = new MutableInteger();
	public MutableInteger maxPositionTimeAtLoss = new MutableInteger();
	public MutableInteger maxPositionTimeAtProfit = new MutableInteger();
	public MutableDouble minPositionTimeAtProfitYield = new MutableDouble();
	public MutableInteger prefillShift = new MutableInteger();
	public MutableDouble disableAfterLoss = new MutableDouble();
	public MutableDouble disableAfterYield = new MutableDouble();
	public boolean enablePrefill;
	public boolean enablePremise;
	public boolean enableContext;
	
	public ArrayList<SignalMetricType> listOfSignalMetricType = new ArrayList<SignalMetricType>();
	
	@Override
	public String toString() {
		String string = new String();
		string += "\n - Can go long: " + canGoLong.value;
		string += "\n - Can go short: " + canGoShort.value;
		string += "\n - Can reenter: " + canReenter.value;
		string += "\n - Enable prefill: " + enablePrefill;
		string += "\n - Enable premise: " + enablePremise;
		string += "\n - Enable context: " + enableContext;
		string += "\n - Disable after nil changes: " + disableAfterNilChanges;
		string += "\n - Disable after nil changes in price: " + maxNilChangePrice;
		string += "\n - Disable after nil changes in volume: " + maxNilChangeVolume;
		string += "\n - Disable after loss: " + disableAfterLoss.value;
		string += "\n - Disable after yield: " + disableAfterYield.value;
		string += "\n - Max position entry time before close: " + maxPositionEntryTime;
		string += "\n - Max position exit time before close: " + maxPositionExitTime;
		string += "\n - Max position time at loss: " + maxPositionTimeAtLoss.value;
		string += "\n - Max position time at profit: " + maxPositionTimeAtProfit.value;
		string += "\n - Min position time at profit yield: " + minPositionTimeAtProfitYield.value;
		string += "\n - Min position age mins before exit from signal allowed: " + minPositionAgeMinsBeforeExit.value;
		string += "\n - Max stop loss percent: " +  maxStopLossPercent.value;
		string += "\n - Max profit drawdown percent: " +  maxProfitDrawdownPercent.value;
		string += "\n - Max profit drawdown afer minutes: " +  invervalForDDorSLExitMins.value;
		string += "\n - Max transactions per day: " + maxTransactionsDay.value;
		string += "\n - Signal point tactic (entry): " + signalPointTacticForEntry.value.name();
		string += "\n - Signal point tactic (exit): " + signalPointTacticForExit.value.name();
		string += "\n - Entry after loss interval minutes: " + intervalForEntryAfterExitWithLossMins.value;
		string += "\n - Entry after exit with same signal point type: " + intervalForEntryWithSameSignalPointType.value;
		string += "\n - Reentry interval minutes: " + intervalForReentryMins.value;
		string += "\n - Reentry maximum frequency: " + maxReenterTimesPerPosition.value;
		string += "\n - Reentry minimum gain: " + minReentryPercentGain.value;

		return string;
	}

	public StrategyOptions copy() {
		return new Cloner().deepClone(this);
	}
}
