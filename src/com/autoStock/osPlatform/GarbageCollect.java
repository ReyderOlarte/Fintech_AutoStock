package com.autoStock.osPlatform;

/**
 * @author Kevin Kowalewski
 *
 */
public class GarbageCollect {
	Thread threadForGC;
	
	public void runGCThread(){
		threadForGC = new Thread(new Runnable(){
			@Override
			public void run() {
				
			}
		});
	}
}
