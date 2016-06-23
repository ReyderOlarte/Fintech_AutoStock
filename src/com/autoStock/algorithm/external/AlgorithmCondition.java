package com.autoStock.algorithm.external;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.position.PositionGovernorResponseStatus;
import com.autoStock.position.PositionHistory;
import com.autoStock.position.PositionHistory.ProfitOrLoss;
import com.autoStock.signal.SignalDefinitions.SignalPointType;
import com.autoStock.signal.SignalPoint;
import com.autoStock.strategy.StrategyOptions;
import com.autoStock.strategy.StrategyResponse;
import com.autoStock.strategy.StrategyResponse.StrategyAction;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.ListTools;
import com.autoStock.trading.types.Position;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.basic.Time;

/**
 * @author Kevin Kowalewski
 *
 */
public class AlgorithmCondition {
	private StrategyOptions strategyOptions;
	
	public AlgorithmCondition(StrategyOptions strategyOptions){
		this.strategyOptions = strategyOptions;
	}
	
	public boolean canTadeAfterTransactions(int transactions){
		if (transactions >= strategyOptions.maxTransactionsDay.value){
			return false;
		}
		
		return true;
	}
	
	public boolean canEnterTradeOnDate(Date date, Exchange exchange){
		Date dateForLastExecution = DateTools.getChangedBySubtracting(DateTools.getDateFromTime(exchange.timeCloseForeign), strategyOptions.maxPositionEntryTime);		
	
		if (date.getHours() > dateForLastExecution.getHours() || ( date.getHours() >= dateForLastExecution.getHours() && date.getMinutes() >= dateForLastExecution.getMinutes())){
			return false;
		}
		
		return true;
	}
	
	public boolean canEnterWithQuoteSlice(QuoteSlice quoteSlice, SignalPoint signalPoint){
		if (signalPoint.signalPointType == SignalPointType.long_entry){
			if (quoteSlice.priceClose >= quoteSlice.priceOpen){
				return true;
			}
		}else if (signalPoint.signalPointType == SignalPointType.short_entry){
			if (quoteSlice.priceClose <= quoteSlice.priceOpen){
				return true;
			}
		}else {
			return true;
		}
		
		return false;
	}
	
	public boolean canTradeAfterLoss(ArrayList<StrategyResponse> listOfStrategyResponse){
		for (StrategyResponse strategyResponse : listOfStrategyResponse){
			if (strategyResponse.positionGovernorResponse != null && strategyResponse.positionGovernorResponse.position != null){
				if (strategyResponse.positionGovernorResponse.position.getPositionProfitLossAfterComission(true) < 0){
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean canTradeAfterLossInterval(Date date, ArrayList<StrategyResponse> listOfStrategyResponse){
		if (strategyOptions.intervalForEntryAfterExitWithLossMins.value == 0){return true;}
		
		StrategyResponse strategyResponse = null;
		
		for (StrategyResponse strategyResponseIn : listOfStrategyResponse){
			if (strategyResponseIn.positionGovernorResponse != null && strategyResponseIn.strategyAction == StrategyAction.algorithm_changed){
				strategyResponse = strategyResponseIn;
			}
		}
		
		if (strategyResponse == null){return true;}
		
		if (strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_exit
			|| strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_exit){
			
			if (strategyResponse.positionGovernorResponse.position.getPositionProfitLossBeforeComission() < 0){
				if ((date.getTime() - strategyResponse.positionGovernorResponse.dateOccurred.getTime()) / 60 /1000 < strategyOptions.intervalForEntryAfterExitWithLossMins.value){
					return false;
				}
			}
		}

		return true;
	}
	
	public boolean stopLoss(Position position){
		if (position.isFilledAndOpen()){
			if (strategyOptions.invervalForDDorSLExitMins.value > 0){
				if (position.getPositionHistory().getAge().asSeconds() >= strategyOptions.invervalForDDorSLExitMins.value * 60){
					return position.getCurrentPercentGainLoss(false) < strategyOptions.maxStopLossPercent.value;			
				}else {
					return false;
				}
			}
			return position.getCurrentPercentGainLoss(false) < strategyOptions.maxStopLossPercent.value;
		}
		
		return false;
	}
	
	public boolean requestExitOnDate(Date date, Exchange exchange){
//		Date dateForLastExecution = DateTools.getChangedDate(DateTools.getDateFromTime(exchange.timeCloseForeign), strategyOptions.maxPositionExitTime);
		
		Time time = DateTools.getTimeFromDate(date);
		Time timeForLastExecution = new Time(exchange.timeCloseForeign.hours, exchange.timeCloseForeign.minutes, exchange.timeCloseForeign.seconds);
		
		if (exchange.timeCloseForeign.minutes == 0){
			timeForLastExecution.hours--;
			timeForLastExecution.minutes = 60 - strategyOptions.maxPositionExitTime;
		}else{
			timeForLastExecution.minutes -= strategyOptions.maxPositionExitTime;
		}
		
		
		if (time.hours > timeForLastExecution.hours || (time.hours >= timeForLastExecution.hours && time.minutes >= timeForLastExecution.minutes)){
			return true;
		}
		
		return false;
	}
	
	public boolean requestExitAfterTimeInLoss(Date date, Position position, ArrayList<StrategyResponse> listOfStrategyResponse){
		if (strategyOptions.maxPositionTimeAtLoss.value == 0){return false;}
		
		//Co.println("--> A: " + ListTools.getLast(position.getPositionHistory().listOfPositionHistory).date + ", " + position.getPositionHistory().getTimeIn(ProfitOrLoss.loss).asSeconds());		
		//Co.println("--> B: " + ListTools.getLast(position.getPositionHistory().listOfPositionHistory).date + ", " + position.getPositionHistory().getTimeIn(ProfitOrLoss.profit).asSeconds());

		if (position.getPositionHistory().getTimeIn(ProfitOrLoss.loss).asSeconds() >= strategyOptions.maxPositionTimeAtLoss.value * 60){
			return true;
		}
		
		return false;
	}
	
	public boolean requestExitAfterTimeInProfit(QuoteSlice quoteSlice, Position position){
		if (strategyOptions.maxPositionTimeAtLoss.value == 0){return false;}
		
		if (position.getPositionHistory().getTimeIn(ProfitOrLoss.profit).asSeconds() >= strategyOptions.maxPositionTimeAtProfit.value * 60){
			if (position.getCurrentPercentGainLoss(true) >= strategyOptions.minPositionTimeAtProfitYield.value){
				return true;
			}
		}
		
		return false;
	}

	public boolean disableAfterNilChanges(ArrayList<QuoteSlice> listOfQuoteSlice) {
		if (strategyOptions.disableAfterNilChanges == false){return false;}
		
		int countOfNilChanges = 0;
		double price = 0;
		
		for (QuoteSlice quoteSlice : listOfQuoteSlice){
			if (quoteSlice.priceClose == price){
				countOfNilChanges++;
			}else{
				countOfNilChanges = 0;
			}
			
			price = quoteSlice.priceClose;
		}
		
		return countOfNilChanges >= strategyOptions.maxNilChangePrice;
	}
	
	public boolean disableAfterNilVolume(ArrayList<QuoteSlice> listOfQuoteSlice){
		if (strategyOptions.disableAfterNilVolumes == false){return false;}
		
		int countOfNilChanges = 0;
		
		for (QuoteSlice quoteSlice : listOfQuoteSlice){
			if (quoteSlice.sizeVolume == 0){
				countOfNilChanges++;
			}else{
				countOfNilChanges = 0;
			}
		}
		
		return countOfNilChanges >= strategyOptions.maxNilChangeVolume;
	}
	
	public boolean disableAfterLoss(AlgorithmBase algorithmBase){
		if (strategyOptions.disableAfterLoss.value != 0 && algorithmBase.getYieldCurrent() <= strategyOptions.disableAfterLoss.value){
			return true;
		}
		return false;
	}
	
	public boolean disableAfterYield(AlgorithmBase algorithmBase){
		if (strategyOptions.disableAfterYield.value != 0 && algorithmBase.getYieldCurrent() >= strategyOptions.disableAfterYield.value){
			return true;
		}
		return false;
	}

	public boolean stopFromProfitDrawdown(Position position) {
		double profitDrawdown = position.getPositionProfitDrawdown();
		double positionMaxProfitPercent = position.getPositionHistory().getMaxPercentProfitLoss().profitLossPercent;
		
		if (strategyOptions.invervalForDDorSLExitMins.value > 0){
			if (position.getPositionHistory().getTimeIn(ProfitOrLoss.profit).asSeconds() >= strategyOptions.invervalForDDorSLExitMins.value * 60){
				return profitDrawdown <= strategyOptions.maxProfitDrawdownPercent.value && position.getCurrentPercentGainLoss(false) > 0;			
			}else {
				return false;
			}
		}else{
			return profitDrawdown <= strategyOptions.maxProfitDrawdownPercent.value && position.getCurrentPercentGainLoss(false) > 0;
		}
	}
	
	public boolean canExitAfterTime(Position position){
		return position.getPositionHistory().getAge().asSeconds() >= strategyOptions.minPositionAgeMinsBeforeExit.value * 60;
	}
	
	public boolean canTradeAfterExitWithSameSignal(Date date, ArrayList<StrategyResponse> listOfStrategyResponse, SignalPoint signalPoint){
		if (strategyOptions.intervalForEntryWithSameSignalPointType.value == 0){return true;}
		
		StrategyResponse strategyResponse = null;
		
		for (StrategyResponse strategyResponseIn : listOfStrategyResponse){
			if (strategyResponseIn.positionGovernorResponse != null && strategyResponseIn.strategyAction == StrategyAction.algorithm_changed){
				strategyResponse = strategyResponseIn;
			}
		}
		
		if (strategyResponse == null){return true;}
	
		if (strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_exit && signalPoint.signalPointType == SignalPointType.long_entry 
			|| strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_exit && signalPoint.signalPointType == SignalPointType.short_entry){
			
			if ((date.getTime() - strategyResponse.positionGovernorResponse.dateOccurred.getTime()) / 60 /1000 < strategyOptions.intervalForEntryWithSameSignalPointType.value){
				return false;
			}
		}
		
		return true;
	}
}
