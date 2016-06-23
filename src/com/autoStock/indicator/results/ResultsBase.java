/**
 * 
 */
package com.autoStock.indicator.results;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.tools.ResultsTools;
import com.autoStock.types.basic.BasicTimeValuePair;

/**
 * @author Kevin Kowalewski
 *
 */
public abstract class ResultsBase {
	public Date[] arrayOfDates;
	public double[] arrayOfValue;
	protected boolean isStandard = true;

	public ResultsBase(int length){
		arrayOfDates = new Date[length];
		arrayOfValue = new double[length];
	}
	
	public ArrayList<BasicTimeValuePair> getResultsAsListOfBasicTimeValuePair(Date[] arrayOfDates, double[] arrayOfValues){
		return ResultsTools.getBasicPair(arrayOfDates, arrayOfValues);
	}
	
	public boolean isStandard(){
		return isStandard;
	}
	
	public double getLast(){
		return arrayOfValue[arrayOfValue.length-1];
	}
}
