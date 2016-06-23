/**
 * 
 */
package com.autoStock.indicator.results;


/**
 * @author Kevin Kowalewski
 *
 */
public class ResultsSTORSI extends ResultsBase {
	public double[] arrayOfPercentK;
	public double[] arrayOfPercentD;
	
	public ResultsSTORSI(int length){
		super(length);
		this.arrayOfPercentK = new double[length]; 
		this.arrayOfPercentD = new double[length];
	}
}
