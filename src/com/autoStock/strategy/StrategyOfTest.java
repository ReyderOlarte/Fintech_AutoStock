package com.autoStock.strategy;

import java.util.ArrayList;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.algorithm.external.AlgorithmCondition;
import com.autoStock.indicator.IndicatorGroup;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.position.PositionGovernorResponse;
import com.autoStock.position.PositionGovernorResponseStatus;
import com.autoStock.position.PositionOptions;
import com.autoStock.signal.SignalDefinitions.SignalPointType;
import com.autoStock.signal.SignalPoint;
import com.autoStock.signal.Signaler;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalSource;
import com.autoStock.signal.SignalGroup;
import com.autoStock.strategy.StrategyResponse.StrategyAction;
import com.autoStock.strategy.StrategyResponse.StrategyActionCause;
import com.autoStock.trading.types.Position;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 * 
 */
public class StrategyOfTest extends StrategyBase {
	public StrategyOfTest(AlgorithmBase algorithmBase) {
		super(algorithmBase);
		
		strategyOptions = StrategyOptionDefaults.getDefaultStrategyOptions();

//		 strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_adx);
//		 strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_di);
//		 strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_rsi);
//		 strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_macd);
//		 strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_trix);

//		strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_cci);
//		 strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_mfi);
//		 strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_roc);
//		 strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_uo);
//		strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_storsi);
//		 strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_ar_up);
//		 strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_ar_down);
//		 strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_willr);
//		 strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_sar);
		// strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_candlestick_group);
		strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_encog);
//		strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_crossover);

	}

	@Override
	public StrategyResponse informStrategy(IndicatorGroup indicatorGroup, SignalGroup signalGroup, ArrayList<QuoteSlice> listOfQuoteSlice, ArrayList<StrategyResponse> listOfStrategyResponse, Position position, PositionOptions positionOptions) {
		StrategyResponse strategyResponse = new StrategyResponse();
		QuoteSlice quoteSlice = listOfQuoteSlice.get(listOfQuoteSlice.size() - 1);
		AlgorithmCondition algorithmCondition = new AlgorithmCondition(strategyOptions);

		signaler = new Signaler(SignalSource.from_algorithm, signalGroup);
		signaler.addSignalBaseFromMetrics(strategyOptions.listOfSignalMetricType);

		// SignalPoint signalPointForEntry = SignalPointMethod.getSignalPoint(false, signal, PositionType.position_none, strategyOptions.signalPointTacticForEntry);

		if (algorithmBase.algorithmState.isDisabled) {
			strategyResponse.positionGovernorResponse = new PositionGovernorResponse();
			strategyResponse.strategyAction = StrategyAction.algorithm_disable;
			strategyResponse.strategyActionCause = StrategyActionCause.cease_disabled;
		} else if (algorithmCondition.disableAfterNilChanges(listOfQuoteSlice)) {
			strategyResponse.positionGovernorResponse = cease(StrategyActionCause.disable_condition_nilchange, quoteSlice, position, strategyResponse);
		} else if (algorithmCondition.disableAfterNilVolume(listOfQuoteSlice)) {
			strategyResponse.positionGovernorResponse = cease(StrategyActionCause.disable_condition_nilvolume, quoteSlice, position, strategyResponse);
		} else if (position != null) {
			if (algorithmCondition.stopLoss(position)) {
				strategyResponse.positionGovernorResponse = exit(StrategyActionCause.cease_condition_stoploss, quoteSlice, position, strategyResponse);
			} else if (algorithmCondition.stopFromProfitDrawdown(position)){
				strategyResponse.positionGovernorResponse = exit(StrategyActionCause.cease_condition_profit_drawdown, quoteSlice, position, strategyResponse);
			} else if (algorithmCondition.requestExitOnDate(quoteSlice.dateTime, algorithmBase.exchange)) {
				strategyResponse.positionGovernorResponse = exit(StrategyActionCause.cease_condition_time_exit, quoteSlice, position, strategyResponse);
			} else if (algorithmCondition.requestExitAfterTimeInLoss(quoteSlice.dateTime, position, listOfStrategyResponse)){
				strategyResponse.positionGovernorResponse = exit(StrategyActionCause.cease_condition_time_loss, quoteSlice, position, strategyResponse);
			} else if (algorithmCondition.requestExitAfterTimeInProfit(quoteSlice, position)){
				strategyResponse.positionGovernorResponse = exit(StrategyActionCause.cease_condition_time_profit, quoteSlice, position, strategyResponse);
			}else if (algorithmCondition.canExitAfterTime(position) == false){
				strategyResponse.positionGovernorResponse.position = position;
				pass(strategyResponse, StrategyActionCause.pass_condition_entry);
			}
			else {
				SignalPoint signalPointForExit = algorithmBase.positionGovernor.resolveExit(strategyOptions, signaler, position);
				strategyResponse.positionGovernorResponse = proceed(quoteSlice, position, null, null, signalPointForExit);
			}
		} else {
			if (algorithmBase.algorithmState.isDisabled) {
				strategyResponse.positionGovernorResponse = cease(StrategyActionCause.cease_disabled, quoteSlice, position, strategyResponse);
			} else if (algorithmCondition.canEnterTradeOnDate(quoteSlice.dateTime, algorithmBase.exchange) == false) {
				strategyResponse.positionGovernorResponse = cease(StrategyActionCause.disable_condition_time_entry, quoteSlice, position, strategyResponse);
			} else if (algorithmCondition.canTadeAfterTransactions(algorithmBase.algorithmState.transactions) == false) {
				strategyResponse.positionGovernorResponse = cease(StrategyActionCause.cease_condition_trans, quoteSlice, position, strategyResponse);
			} else if (algorithmCondition.disableAfterLoss(algorithmBase)){
				strategyResponse.positionGovernorResponse = cease(StrategyActionCause.disable_condition_profit_loss, quoteSlice, position, strategyResponse);
			} else if (algorithmCondition.disableAfterYield(algorithmBase)){
				strategyResponse.positionGovernorResponse = cease(StrategyActionCause.disable_condition_profit_yield, quoteSlice, position, strategyResponse);
			}else if (algorithmCondition.canTradeAfterLossInterval(quoteSlice.dateTime, listOfStrategyResponse) == false){
				pass(strategyResponse, StrategyActionCause.pass_condition_previous_loss);
			}
			
//			else if (algorithmCondition.canTradeLongAfterExitWithSignalPoint(quoteSlice.dateTime, listOfStrategyResponse) == false){
//				pass(strategyResponse, StrategyActionCause.pass_condition_previous_exit_long);
//			}else if (algorithmCondition.canTradeShortAfterExitWithSignalPoint(quoteSlice.dateTime, listOfStrategyResponse) == false){
//				pass(strategyResponse, StrategyActionCause.pass_condition_previous_exit_short);
//			}
			// else if (algorithmCondition.canEnterWithQuoteSlice(quoteSlice, signalPointForEntry) == false){
			// strategyResponse.positionGovernorResponse = new PositionGovernorResponse();
			// strategyResponse.strategyAction = StrategyAction.algorithm_pass;
			// strategyResponse.strategyActionCause = StrategyActionCause.pass_condition_quotslice;
			// }
			else {
				SignalPoint signalPointForEntry = algorithmBase.positionGovernor.resolveEntry(strategyOptions, signaler);
				
				if (algorithmCondition.canTradeAfterExitWithSameSignal(quoteSlice.dateTime, listOfStrategyResponse, signalPointForEntry) == false && (signalPointForEntry.signalPointType == SignalPointType.long_entry || signalPointForEntry.signalPointType == SignalPointType.short_entry)){
					pass(strategyResponse, StrategyActionCause.pass_condition_previous_exit_signal_same);
				} else{
					strategyResponse.positionGovernorResponse = proceed(quoteSlice, position, positionOptions, signalPointForEntry, null);					
				}
			}
		}

		strategyResponse.signaler = signaler;
		strategyResponse.signaler.generateSignalMoments(position);
		strategyResponse.quoteSlice = quoteSlice;
		strategyResponse.basicAccountCopy = algorithmBase.basicAccount.copy();

		return formulateStrategyResponse(strategyResponse);
	}
	
	@Override
	public StrategyResponse requestExit(Position position, QuoteSlice quoteSlice, PositionOptions positionOptions) {
		StrategyResponse strategyResponse = new StrategyResponse();
		strategyResponse.signaler = new Signaler(SignalSource.from_manual, null);
		strategyResponse.quoteSlice = quoteSlice;
		strategyResponse.positionGovernorResponse = cease(StrategyActionCause.cease_end_of_feed, quoteSlice, position, strategyResponse);
		strategyResponse.basicAccountCopy = algorithmBase.basicAccount.copy();
		
		return formulateStrategyResponse(strategyResponse);
	}

	private StrategyResponse formulateStrategyResponse(StrategyResponse strategyResponse) {
		if (strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.failed) {
			strategyResponse.strategyAction = StrategyAction.algorithm_changed;
			strategyResponse.strategyActionCause = StrategyActionCause.position_governor_failure;
		} else if (strategyResponse.positionGovernorResponse.status != PositionGovernorResponseStatus.none) {
			if (didPositionGovernorChangePosition(strategyResponse.positionGovernorResponse)) {
				if (strategyResponse.strategyAction == StrategyAction.none) {
					strategyResponse.strategyActionCause = StrategyActionCause.proceed_changed;
					strategyResponse.strategyAction = StrategyAction.algorithm_changed;
				} else {
					// ?
				}
			}
		}
		
		currentStrategyResponse = strategyResponse;
		
		if (strategyResponse.strategyAction == lastStrategyResponse.strategyAction && strategyResponse.strategyActionCause == strategyResponse.strategyActionCause && didPositionGovernorChangePosition(strategyResponse.positionGovernorResponse) == false) {
			strategyResponse.strategyAction = StrategyAction.no_change;
			strategyResponse.strategyActionCause = StrategyActionCause.none;
			return strategyResponse;
		} else {
			lastStrategyResponse = strategyResponse;
			return strategyResponse;
		}
	}
	
	private void pass(StrategyResponse strategyResponse, StrategyActionCause strategyActionCause){
		strategyResponse.strategyAction = StrategyAction.algorithm_pass;
		strategyResponse.strategyActionCause = strategyActionCause;
	}

	private PositionGovernorResponse proceed(QuoteSlice quoteSlice, Position position, PositionOptions positionOptions, SignalPoint signalPointForEntry, SignalPoint signalPointForExit) {
		// Co.println("--> Asked to proceed");
		PositionGovernorResponse positionGovernorResponse = algorithmBase.positionGovernor.informGovener(signalPointForEntry, signalPointForExit, quoteSlice, signaler, algorithmBase.exchange, strategyOptions, false, position, positionOptions, algorithmBase.basicAccount);
		return positionGovernorResponse;
	}

	private PositionGovernorResponse cease(StrategyActionCause strategyActionCause, QuoteSlice quoteSlice, Position position, StrategyResponse strategyResponse) {
		// Co.println("--> Asked to cease: " + strategyActionCause.name());
		PositionGovernorResponse positionGovernorResponse = new PositionGovernorResponse();
		if (position != null) {
			positionGovernorResponse = algorithmBase.positionGovernor.informGovener(null, null, quoteSlice, signaler, algorithmBase.exchange, strategyOptions, true, position, null, algorithmBase.basicAccount);
		}
		strategyResponse.strategyAction = StrategyAction.algorithm_disable;
		strategyResponse.strategyActionCause = strategyActionCause;

		return positionGovernorResponse;
	}

	private PositionGovernorResponse exit(StrategyActionCause strategyActionCause, QuoteSlice quoteSlice, Position position, StrategyResponse strategyResponse) {
		// Co.println("--> Asked to exit");
		PositionGovernorResponse positionGovernorResponse = algorithmBase.positionGovernor.informGovener(null, null, quoteSlice, signaler, algorithmBase.exchange, strategyOptions, true, position, null, algorithmBase.basicAccount);

		strategyResponse.strategyAction = StrategyAction.algorithm_changed;
		strategyResponse.strategyActionCause = strategyActionCause;

		return positionGovernorResponse;
	}

	private boolean didPositionGovernorChangePosition(PositionGovernorResponse positionGovernorResponse) {
		if (positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_entry || positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_reentry || positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_exit || positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_entry || positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_reentry || positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_exit) {
			return true;
		} else {
			return false;
		}
	}
}
