package com.autoStock.internal;

/**
 * @author Kevin Kowalewski
 *
 */
public class CallbackLock {
	private volatile int callbacks;
	
	public void requestLock(){
		synchronized (this) {
			callbacks++;
		}
	}
	
	public void releaseLock(){
		synchronized (this) {
			callbacks--;	
		}
	}
	
	public boolean isLocked(){
		if (callbacks > 0){
			return true;
		}
		return false;
	}
}
