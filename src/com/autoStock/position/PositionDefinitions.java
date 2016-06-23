/**
 * 
 */
package com.autoStock.position;

/**
 * @author Kevin Kowalewski
 *
 */
public class PositionDefinitions {
	
	public static enum PositionReason {
		reason_signal_threshold,
		reason_algorith_condition,
		reason_time,
		reason_none,
	}
	
	public static enum PositionType {
		position_long_entry,
		position_short_entry,
		position_long,
		position_short,
		position_long_exit,
		position_short_exit,
		
		position_long_exited,
		position_short_exited,
		
		position_cancelling,
		position_cancelled,
		position_failed,
		position_none,
		;
	}
	
	public static enum PositionReality {
		reality_synthesized,
		reality_submit,
	}
}
