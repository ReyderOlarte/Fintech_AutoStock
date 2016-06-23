package com.autoStock.position;

import com.autoStock.trading.types.Position;

/**
 * @author Kevin Kowalewski
 *
 */
public interface ListenerOfPositionStatusChange {
	public void positionStatusChanged(Position position);
}
