package com.autoStock.adjust;

import java.util.Random;


/**
 * @author Kevin Kowalewski
 * 
 */
public abstract class IterableBase {
	protected int currentIndex = 0;
	protected boolean iterateCausesRebase = false;
	public boolean rebaseRequired = false;
	
	public abstract boolean hasMore();
	public abstract boolean isDone();
	public abstract boolean skip();
	public abstract int getMaxIndex();
	public abstract int getMaxValues();

	public void iterate() {
		currentIndex++;
	}

	public void reset() {
		currentIndex = 0;
	}
	
	public void randomize(Random random){
		currentIndex = random.nextInt(getMaxIndex()+1);
	}
	
	public void overrideAndSetCurrentIndex(int currentIndex){
		this.currentIndex = currentIndex;
	}
	
	public int getCurrentIndex(){
		return currentIndex;
	}
	
	public void setCurrentIndex(int index){
		if (currentIndex > getMaxIndex()){
			throw new IllegalArgumentException("index > max");
		}
		currentIndex = index;
	}
	
	public double getPercentComplete(){
		return (double)getMaxIndex() / (double)currentIndex;
	}
}
