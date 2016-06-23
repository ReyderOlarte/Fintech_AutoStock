package com.autoStock.position;

public enum PositionGovernorResponseReason{
	failed_insufficient_funds,
	algorithm_condition_time,
	algorithm_condition_trans,
	algorithm_condition_profit,
	algorithm_condition_stoploss,
	algorithm_is_disabled,
	none
}