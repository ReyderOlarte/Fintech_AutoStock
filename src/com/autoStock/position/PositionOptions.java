package com.autoStock.position;

/**
 * @author Kevin Kowalewski
 * 
 */
public class PositionOptions {
	public ListenerOfPositionStatusChange listenerOfPositionStatusChange;

	public PositionOptions() {
		
	}

	public PositionOptions(ListenerOfPositionStatusChange listenerOfPositionStatusChange) {
		this.listenerOfPositionStatusChange = listenerOfPositionStatusChange;
	}
}
