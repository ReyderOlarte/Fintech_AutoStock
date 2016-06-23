package com.autoStock.position;

public enum PositionGovernorResponseStatus {
	changed_long_entry,
	changed_short_entry,
	changed_long_reentry,
	changed_short_reentry,
	changed_long_exit,
	changed_short_exit,
	
	failed,
	none,
	;
}