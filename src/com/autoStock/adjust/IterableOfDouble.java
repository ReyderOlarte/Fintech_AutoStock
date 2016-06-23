package com.autoStock.adjust;
import com.autoStock.tools.MathTools;


/**
 * @author Kevin Kowalewski
 *
 */
public class IterableOfDouble extends IterableBase {
	private double min;
	private double max;
	private double step;
	
	public IterableOfDouble(double min, double max, double step, boolean iterateCausesRebase){
		this.min = min;
		this.max = max;
		this.step = step;
		
		if ((min - max) % step != 0){
//			throw new IllegalArgumentException();
		}
		
		if (iterateCausesRebase){
			rebaseRequired = true;
		}
	}
	
	public IterableOfDouble(double min, double max, double step){
		this(min, max, step, false);
	}
	
	public void rebase(double min, double max){	
		while ((min - max) % step != 0){
			max++;
		}
		
		this.min = min;
		this.max = max;
	}
	
	public double getDouble(){
		return MathTools.round((min + (step * currentIndex)));
	}

	@Override
	public boolean hasMore() {
		return currentIndex <= getMaxIndex();
	}

	@Override
	public void reset() {
		super.reset();
	}

	@Override
	public int getMaxIndex() {
		return (int) ((double) (max - min) / step);
	}

	@Override
	public int getMaxValues() {
		return getMaxIndex() + 1;
	}
	
	public double getMin(){
		return min;
	}
	
	public double getMax(){
		return max;
	}
	
	public double getStep() {
		return step;
	}
	
	@Override
	public void iterate() {
		super.iterate();
		
		if (iterateCausesRebase){
			rebaseRequired = true;
		}
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
