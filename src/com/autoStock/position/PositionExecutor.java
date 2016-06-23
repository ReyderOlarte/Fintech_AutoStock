package com.autoStock.position;

import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.trading.types.Position;

/**
 * @author Kevin Kowalewski
 * 
 */
public class PositionExecutor {

	public void executeLongEntry(Position position) {
		position.executePosition();
	}

	public void executeShortEntry(Position position) {
		position.executePosition();
	}

	public void executeLongExit(Position position) {
		if (position.positionType == PositionType.position_long_entry){
			position.cancelEntry();
		}else{
			position.positionType = PositionType.position_long_exit;
			position.executePosition();
		}
	}

	public void executeShortExit(Position position) {
		if (position.positionType == PositionType.position_short_entry){
			position.cancelEntry();
		}else{
			position.positionType = PositionType.position_short_exit;
			position.executePosition();
		}
	}
}
