package com.autoStock.signal;

public class SignalRangeLimit {
	private double max;
	private double min;
	
	public SignalRangeLimit(){
		reset();
	}
	
	public SignalRangeLimit(double min, double max){
		this.min = min;
		this.max = max;
	}
	
	public void addValue(double value){
		min = Math.min(min, value);
		max = Math.max(max, value);
	}
	
	public boolean isSet(){
		if (min != Double.POSITIVE_INFINITY && max != Double.NEGATIVE_INFINITY){
			return true;
		}
		return false;
	}
	
	public void reset(){
		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;
	}	
	
	public double getMin(){
		if (min == Double.POSITIVE_INFINITY){throw new IllegalAccessError("Can't access min because it was never set");}
		return min;
	}
	
	public double getMax(){
		if (max == Double.NEGATIVE_INFINITY){throw new IllegalAccessError("Can't access max because it was never set");}
		return max;
	}
	
	public SignalRangeLimit copy(){
		return new SignalRangeLimit(min, max);
	}
}
