package com.autoStock.position;

import java.util.ArrayList;

import com.autoStock.Co;
import com.autoStock.account.BasicAccount;
import com.autoStock.chart.CombinedLineChart.StoredSignalPoint;
import com.autoStock.misc.Pair;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.Signaler;
import com.autoStock.signal.SignalDefinitions.SignalPointType;
import com.autoStock.signal.SignalPoint;
import com.autoStock.signal.TacticResolver;
import com.autoStock.strategy.ReentrantStrategy;
import com.autoStock.strategy.ReentrantStrategy.ReentryStatus;
import com.autoStock.strategy.StrategyOptions;
import com.autoStock.trading.types.Position;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class PositionGovernor {
	private PositionManager positionManager;
	private ArrayList<Pair<Symbol,ArrayList<PositionGovernorResponse>>> listOfPairedResponses = new ArrayList<Pair<Symbol,ArrayList<PositionGovernorResponse>>>();
	private ReentrantStrategy reentrantStrategy = new ReentrantStrategy();
	private PositionGenerator positionGenerator = new PositionGenerator();
	public ArrayList<StoredSignalPoint> listOfPredSignalPoint;
	
	public PositionGovernor(PositionManager positionManager){
		this.positionManager = positionManager;
	}
	
	public SignalPoint resolveEntry(StrategyOptions strategyOptions, Signaler signaler){
		return TacticResolver.getSignalPoint(signaler, strategyOptions.signalPointTacticForEntry.value, null);
	}
	
	public SignalPoint resolveExit(StrategyOptions strategyOptions, Signaler signaler, Position position){
		return TacticResolver.getSignalPoint(signaler, strategyOptions.signalPointTacticForExit.value, position);
	}
	
	public PositionGovernorResponse informGovener(SignalPoint signalPointForEntry, SignalPoint signalPointForExit, QuoteSlice quoteSlice, Signaler signaler, Exchange exchange, StrategyOptions strategyOptions, boolean requestExit, Position position, PositionOptions positionOptions, BasicAccount basicAccount){
		PositionGovernorResponse positionGovernorResponse = new PositionGovernorResponse();
		SignalPoint signalPoint = new SignalPoint();
		Pair<SignalPoint, Position> pairForPred = listOfPredSignalPoint == null ? null : getSignalPointPred(quoteSlice, signaler, positionGovernorResponse, positionOptions, basicAccount, position, exchange);
		
		if (signalPointForEntry == null){signalPointForEntry = new SignalPoint();}
		if (signalPointForExit == null){signalPointForExit = new SignalPoint();}
		
		if (pairForPred != null){
			signalPoint = pairForPred.first;
			if (pairForPred.second != null){position = pairForPred.second;}
		} else {
			if (position == null){
				if (listOfPredSignalPoint == null){
					signalPoint = signalPointForEntry; //resolveEntry(strategyOptions, signaler);
					
					if (signalPoint.signalPointType == SignalPointType.long_entry && strategyOptions.canGoLong.value){
						position = governLongEntry(quoteSlice, signaler, positionGovernorResponse, exchange, positionOptions, basicAccount);
					}else if (signalPoint.signalPointType == SignalPointType.short_entry && strategyOptions.canGoShort.value){
						position = governShortEntry(quoteSlice, signaler, positionGovernorResponse, exchange, positionOptions, basicAccount);
					}
				}
			} else {
				SignalPoint signalPointForReentry = null; //SignalPointResolver.getSignalPoint(false, signal, PositionType.position_none, strategyOptions.signalPointTacticForReentry);
				signalPoint = signalPointForExit; //resolveExit(strategyOptions, signaler, position);
	
				if (position.positionType == PositionType.position_long || position.positionType == PositionType.position_long_entry) {
					if (requestExit || signalPoint.signalPointType == SignalPointType.long_exit) {
						governLongExit(quoteSlice, position, signaler, positionGovernorResponse, exchange);
					}else if (strategyOptions.canReenter.value){ 
						if (reentrantStrategy.getReentryStatus(position, signaler, strategyOptions, signalPointForReentry, getPair(quoteSlice.symbol), quoteSlice) == ReentryStatus.status_reenter){
							governLongReentry(quoteSlice, position, signaler, positionGovernorResponse, exchange, basicAccount);
						}
					}
				}else if (position.positionType == PositionType.position_short || position.positionType == PositionType.position_short_entry) {
					if (requestExit || signalPoint.signalPointType == SignalPointType.short_exit) {
						governShortExit(quoteSlice, position, signaler, positionGovernorResponse, exchange);
					}else if (strategyOptions.canReenter.value){
						//signalPoint.signalPointType == SignalPointType.reentry && 
						if (reentrantStrategy.getReentryStatus(position, signaler, strategyOptions, signalPointForReentry, getPair(quoteSlice.symbol), quoteSlice) == ReentryStatus.status_reenter){
							governShortReentry(quoteSlice, position, signaler, positionGovernorResponse, exchange, basicAccount);
						}
					}
				}else if (position.positionType == PositionType.position_cancelled || position.positionType == PositionType.position_cancelling || position.positionType == PositionType.position_long_exited || position.positionType == PositionType.position_short_exited || position.positionType == PositionType.position_long_exit || position.positionType == PositionType.position_short_exit){
					Co.println("--> Position is not yet removed: " + position.symbol.name);
				}else {
					throw new IllegalStateException("Position type did not match: " + position.positionType.name() + ", " + positionManager.getPositionListSize());
				}
			}
		}

		if (position != null){positionGovernorResponse.positionValue = position.getPositionValue();}
		positionGovernorResponse.position = position;
		positionGovernorResponse.signalPoint = signalPoint;
		positionGovernorResponse.dateOccurred = quoteSlice.dateTime;
		signaler.currentSignalPoint = signalPoint;
		
		if (getPair(quoteSlice.symbol) == null){
			synchronized (listOfPairedResponses) {
				listOfPairedResponses.add(new Pair<Symbol,ArrayList<PositionGovernorResponse>>(quoteSlice.symbol, new ArrayList<PositionGovernorResponse>()));				
			}
		}
		
		if (positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_entry
			|| positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_reentry
			|| positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_exit
			|| positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_entry
			|| positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_reentry
			|| positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_exit){
				getPair(quoteSlice.symbol).second.add(positionGovernorResponse);	
		}

		return positionGovernorResponse;
	} 
	
	private Pair<SignalPoint,Position> getSignalPointPred(QuoteSlice quoteSlice, Signaler signal, PositionGovernorResponse positionGovernorResponse, PositionOptions positionOptions, BasicAccount basicAccount, Position position, Exchange exchange){
		Pair<SignalPoint, Position> pair = new Pair<SignalPoint, Position>(new SignalPoint(), null);
		boolean changed = false;
		
		for (StoredSignalPoint csp : listOfPredSignalPoint){
			if (quoteSlice.dateTime.getTime() == csp.date.getTime()){
				if (csp.signalPointType == SignalPointType.long_entry && position == null){pair.second = governLongEntry(quoteSlice, signal, positionGovernorResponse, exchange, positionOptions, basicAccount); changed = true;}
				else if (csp.signalPointType == SignalPointType.short_entry && position == null){pair.second = governShortEntry(quoteSlice, signal, positionGovernorResponse, exchange, positionOptions, basicAccount); changed = true;}
				else if (csp.signalPointType == SignalPointType.reentry && position != null && position.positionType == PositionType.position_long){governLongReentry(quoteSlice, position, signal, positionGovernorResponse, exchange, basicAccount); changed = true;}
				else if (csp.signalPointType == SignalPointType.reentry && position != null && position.positionType == PositionType.position_short){governShortReentry(quoteSlice, position, signal, positionGovernorResponse, exchange, basicAccount); changed = true;}
				else if (csp.signalPointType == SignalPointType.long_exit && position != null){governLongExit(quoteSlice, position, signal, positionGovernorResponse, exchange); changed = true;}
				else if (csp.signalPointType == SignalPointType.short_exit && position != null){governShortExit(quoteSlice, position, signal, positionGovernorResponse, exchange); changed = true;}
				//else {throw new IllegalArgumentException("Can't handle Chart Signal Point: " + csp.signalPoint + ", " + position + ", " + changed);}
				
				if (changed){
					pair.first.signalPointType = csp.signalPointType;
					pair.first.signalMetricType = SignalMetricType.injected;
					return pair;
				}
			}
		}
		
		return null;
	}
	
	private Position governLongEntry(QuoteSlice quoteSlice, Signaler signal, PositionGovernorResponse positionGovernorResponse, Exchange exchange, PositionOptions positionOptions, BasicAccount basicAccount){
		Position position = positionManager.executePosition(quoteSlice, exchange, signal, PositionType.position_long_entry, null, positionOptions, basicAccount);
		if (position == null){
			positionGovernorResponse.getFailedResponse(PositionGovernorResponseReason.failed_insufficient_funds);
		}else{
			positionGovernorResponse.status = PositionGovernorResponseStatus.changed_long_entry;
		}
		
		return position;
	}
	
	private void governLongReentry(QuoteSlice quoteSlice, Position position, Signaler signal, PositionGovernorResponse positionGovernorResponse, Exchange exchange, BasicAccount basicAccount){
		int reentryUnits = positionGenerator.getPositionReentryUnits(quoteSlice.priceClose, signal, position.basicAccount);
		if (reentryUnits > 0){
			position.executeReentry(reentryUnits, quoteSlice.priceClose);
			positionGovernorResponse.status = PositionGovernorResponseStatus.changed_long_reentry;
		}else{
			positionGovernorResponse.getFailedResponse(PositionGovernorResponseReason.failed_insufficient_funds);
		}
	}
	
	private Position governShortEntry(QuoteSlice quoteSlice, Signaler signal, PositionGovernorResponse positionGovernorResponse, Exchange exchange, PositionOptions positionOptions, BasicAccount basicAccount){
		Position position = positionManager.executePosition(quoteSlice, exchange, signal, PositionType.position_short_entry, null, positionOptions, basicAccount);
		if (position == null){
			positionGovernorResponse.getFailedResponse(PositionGovernorResponseReason.failed_insufficient_funds);
		}else{
			positionGovernorResponse.status = PositionGovernorResponseStatus.changed_short_entry;
		}
		
		return position;
	}

	private void governShortReentry(QuoteSlice quoteSlice, Position position, Signaler signal, PositionGovernorResponse positionGovernorResponse, Exchange exchange, BasicAccount basicAccount){
		int reentryUnits = positionGenerator.getPositionReentryUnits(quoteSlice.priceClose, signal, position.basicAccount);
		if (reentryUnits > 0){
			position.executeReentry(reentryUnits, quoteSlice.priceClose);
			positionGovernorResponse.status = PositionGovernorResponseStatus.changed_short_reentry;
		}else{
			positionGovernorResponse.getFailedResponse(PositionGovernorResponseReason.failed_insufficient_funds);
		}
	}
	
	private void governLongExit(QuoteSlice quoteSlice, Position position, Signaler signal, PositionGovernorResponse positionGovernorResponse, Exchange exchange){
		if (position != null && (position.positionType == PositionType.position_long_exit)){
			throw new IllegalStateException();
		}
		position = positionManager.executePosition(quoteSlice, exchange, signal, PositionType.position_long_exit, position, null, position.basicAccount);
		positionGovernorResponse.status = PositionGovernorResponseStatus.changed_long_exit;
	}
	
	private void governShortExit(QuoteSlice quoteSlice, Position position, Signaler signal, PositionGovernorResponse positionGovernorResponse, Exchange exchange){
		if (position != null && (position.positionType == PositionType.position_short_exit)){
			throw new IllegalStateException();
		}
		position = positionManager.executePosition(quoteSlice, exchange, signal, PositionType.position_short_exit, position, null, position.basicAccount);
		positionGovernorResponse.status = PositionGovernorResponseStatus.changed_short_exit;
	}
	
	public PositionManager getPositionManager(){
		return positionManager;
	}
	
	private Pair<Symbol,ArrayList<PositionGovernorResponse>> getPair(Symbol symbol){
		synchronized (listOfPairedResponses){
			for (Pair<Symbol,ArrayList<PositionGovernorResponse>> pair : listOfPairedResponses){
				if (pair.first.name.equals(symbol.name)){
					return pair;
				}
			}
			return null;
		}
	}
	
	public void reset(){
		listOfPairedResponses.clear();			
	}
}
