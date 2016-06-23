package com.autoStock.backtest;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Kevin Kowalewski
 *
 */
public class BacktestRevolver {
	private ArrayList<BacktestRevolverListener> listOfBacktestRevolverListener = new ArrayList<BacktestRevolverListener>();
	private AtomicInteger atomicIntForCount = new AtomicInteger();
	
	public void addListener(BacktestRevolverListener backtestRevolverListener){
		listOfBacktestRevolverListener.add(backtestRevolverListener);
	}
	
	public void removeListener(BacktestRevolverListener backtestRevolverListener){
		listOfBacktestRevolverListener.remove(backtestRevolverListener);
	}
	
	public void revolve(){
		atomicIntForCount.set(listOfBacktestRevolverListener.size());
		
		for (BacktestRevolverListener listener : listOfBacktestRevolverListener){
			listener.proceedFeed();
		}
	}
	
	public void completed(){
		if (atomicIntForCount.decrementAndGet() == 0){
			
		}
	}
}
