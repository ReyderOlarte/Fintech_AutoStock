package com.autoStock.signal.evaluation;

import com.autoStock.tools.MathTools;

/**
 * @author Kevin Kowalewski
 *
 */
public class DetectorTools {
	public boolean detectPeak(){
		return true;
	}
	
	public boolean detectTrough(){
		return true;
	}
	
	public int getChangeFromPeak(int[] arrayOfInteger){
		int max = MathTools.getMax(arrayOfInteger);
		return arrayOfInteger[arrayOfInteger.length-1] - max;
	}
	
	public int getChangeFromTrough(int[] arrayOfInteger){
		int min = MathTools.getMin(arrayOfInteger);
		return arrayOfInteger[arrayOfInteger.length-1] - min;
	}
	
	public int getChangeBetweenMaxAndMin(int[] arrayOfInteger){
		int min = MathTools.getMin(arrayOfInteger);
		int max = MathTools.getMax(arrayOfInteger);
		
		return max - min;
	}
	
	public boolean averageDirectionIsDown(int[] arrayOfInteger, int something){
		int increases = 0;
		int decreases = 0;
	
		for (int i=1; i < arrayOfInteger.length; i++){
			int last = arrayOfInteger[i-1];
			int current = arrayOfInteger[i];
			
			if (current >= last){
				increases++;
			}else{
				decreases++;
			}
		}
		
		return increases < decreases;
	}
	
	public boolean averageDirectionIsUp(int[] arrayOfInteger, int something){
		int increases = 0;
		int decreases = 0;
	
		for (int i=1; i < arrayOfInteger.length; i++){
			int last = arrayOfInteger[i-1];
			int current = arrayOfInteger[i];
			
			if (current >= last){
				increases++;
			}else{
				decreases++;
			}
		}
		
		return increases > decreases;
	}
	
	public boolean directionIsDown(int[] arrayOfInteger, int allowedUp){
		int lastInteger = Integer.MAX_VALUE;
		for (int integer : arrayOfInteger){
			if (integer > lastInteger){
				if (allowedUp == 0){
					return false;
				}else{
					allowedUp--;
				}
			}
			lastInteger = integer;
		}
		
//		Co.println("--> Direction down on length: " + arrayOfInteger.length);
		
		return true;
	}
	
	public boolean directionIsUp(int[] arrayOfInteger, int allowedDown){
		int lastInteger = Integer.MIN_VALUE;
		for (int integer : arrayOfInteger){
			if (integer < lastInteger){
				if (allowedDown == 0){
					return false;
				}else{
					allowedDown--;
				}
			}
			lastInteger = integer;
		}
		
//		Co.println("--> Direction up on length: " + arrayOfInteger.length);
		
		return true;
	}
}
