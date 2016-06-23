package com.autoStock.strategy;

import java.util.ArrayList;

import com.autoStock.misc.Pair;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.position.PositionGovernorResponse;
import com.autoStock.position.PositionGovernorResponseStatus;
import com.autoStock.signal.Signaler;
import com.autoStock.signal.SignalPoint;
import com.autoStock.tools.DateTools;
import com.autoStock.trading.types.Position;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;
import com.autoStock.types.basic.Time;

/**
 * @author Kevin Kowalewski
 *
 */
public class ReentrantStrategy {
	public static enum ReentryStatus {
		status_reenter,
		status_none,
	}
	
	public ReentryStatus getReentryStatus(Position position, Signaler signal, StrategyOptions strategyOptions, SignalPoint signalPoint, Pair<Symbol, ArrayList<PositionGovernorResponse>> listOfPair, QuoteSlice quoteSlice){
		PositionGovernorResponse positionGovernorResponseLast = listOfPair.second.get(listOfPair.second.size()-1);
		Time timeOfLastOccurrenceDifference = DateTools.getTimeUntilDate(quoteSlice.dateTime, positionGovernorResponseLast.dateOccurred);
		double percentGainFromPosition = position.getCurrentPercentGainLoss(true);
		int reenteredCount = 0;
		
		for (PositionGovernorResponse positionGovernorResponse : listOfPair.second){
			if (positionGovernorResponse.position == position){
				if (positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_reentry || positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_reentry){
					reenteredCount++;
				}
			}
		}

		if (position.positionType == PositionType.position_long || position.positionType == PositionType.position_short){
			if ((timeOfLastOccurrenceDifference.minutes >= strategyOptions.intervalForReentryMins.value || timeOfLastOccurrenceDifference.hours > 0) && reenteredCount < strategyOptions.maxReenterTimesPerPosition.value){
				if (percentGainFromPosition > strategyOptions.minReentryPercentGain.value){
					return ReentryStatus.status_reenter;
				}else{
//					Co.println("--> Percent gain insufficient: " + percentGainFromPosition);
				}
			}else{
//				Co.println("--> Time difference insufficient or reenteredCount > 8" + reenteredCount);
			}
		}
		
		return ReentryStatus.status_none;
	}
}
