/**
 * 
 */
package com.autoStock.tools;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.types.basic.BasicTimeValuePair;

/**
 * @author Kevin Kowalewski
 *
 */
public class ResultsTools {
	public static ArrayList<BasicTimeValuePair> getBasicPair(Date[] arrayOfDates, double[] arrayOfValues){
		ArrayList<BasicTimeValuePair> listOfBasicTimeValuePair = new ArrayList<BasicTimeValuePair>();
		
		for (int i=0; i<Math.min(arrayOfDates.length, arrayOfValues.length); i++){
			listOfBasicTimeValuePair.add(new BasicTimeValuePair(arrayOfDates[i], String.valueOf(arrayOfValues[i])));
		}
		
		return listOfBasicTimeValuePair;
	}
	
	public static ArrayList<BasicTimeValuePair> getBasicPair(Date[] arrayOfDates, float[] arrayOfValues){
		ArrayList<BasicTimeValuePair> listOfBasicTimeValuePair = new ArrayList<BasicTimeValuePair>();
		
		for (int i=0; i<Math.min(arrayOfDates.length, arrayOfValues.length); i++){
			listOfBasicTimeValuePair.add(new BasicTimeValuePair(arrayOfDates[i], String.valueOf(arrayOfValues[i])));
		}
		
		return listOfBasicTimeValuePair;
	}
	
	public static ArrayList<BasicTimeValuePair> getBasicPair(Date[] arrayOfDates, int[] arrayOfValues){
		ArrayList<BasicTimeValuePair> listOfBasicTimeValuePair = new ArrayList<BasicTimeValuePair>();
		
		for (int i=0; i<Math.min(arrayOfDates.length, arrayOfValues.length); i++){
			listOfBasicTimeValuePair.add(new BasicTimeValuePair(arrayOfDates[i], String.valueOf(arrayOfValues[i])));
		}
		
		return listOfBasicTimeValuePair;
	}
}
