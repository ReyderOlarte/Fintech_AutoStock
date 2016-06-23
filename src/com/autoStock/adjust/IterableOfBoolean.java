package com.autoStock.adjust;

/**
 * @author Kevin Kowalewski
 *
 */
public class IterableOfBoolean extends IterableBase {	
	public boolean getBoolean(){
		if (currentIndex == 0){
			return false;
		}else if (currentIndex == 1){
			return true;
		}else{
			throw new IllegalStateException();
		}
	}

	@Override
	public boolean hasMore() {
		return currentIndex <= getMaxIndex();
	}

	@Override
	public int getMaxIndex() {
		return 1;
	}

	@Override
	public int getMaxValues() {
		return 2;
	}

	@Override
	public boolean isDone() {
		return currentIndex == getMaxIndex();
	}

	@Override
	public boolean skip() {
		return false;
	}
}
