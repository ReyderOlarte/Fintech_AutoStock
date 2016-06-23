package com.autoStock.strategy;

import com.autoStock.account.BasicAccount;
import com.autoStock.position.PositionGovernorResponse;
import com.autoStock.signal.Signaler;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 *
 */
public class StrategyResponse {
	public PositionGovernorResponse positionGovernorResponse = new PositionGovernorResponse();
	public StrategyAction strategyAction = StrategyAction.none;
	public StrategyActionCause strategyActionCause = StrategyActionCause.none;
	public QuoteSlice quoteSlice;
	public Signaler signaler;
	public BasicAccount basicAccountCopy;
	
	public enum StrategyAction {
		algorithm_disable,
		algorithm_pass,
		algorithm_proceed,
		algorithm_changed,
		no_change,
		none,
	}
	
	public enum StrategyActionCause {
		/* Will disable the algorithm from any further trades */
		disable_condition_time_entry,
		disable_condition_profit_loss,
		disable_condition_profit_yield,
		disable_condition_nilchange,
		disable_condition_nilvolume,
		
		/* Will cease trading, exiting the current position */
		cease_condition_time_exit,
		cease_condition_time_profit,
		cease_condition_time_loss,
		cease_condition_trans,
		cease_condition_profit,
		cease_condition_stoploss,
		cease_condition_profit_drawdown,
		cease_condition_loss,
		cease_end_of_feed,
		cease_disabled,
		
		/* Will not allow new positions / orders for the under a condition */
		pass_condition_quotslice,
		pass_condition_entry,
		pass_condition_previous_loss,
		pass_condition_previous_exit_signal_same,
		
		/* Other things */
		position_governor_failure,
		proceed_changed,
		none,
	}
}
