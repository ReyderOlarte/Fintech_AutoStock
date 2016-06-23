/**
 * 
 */
package com.autoStock.indicator.results;


/**
 * @author Kevin Kowalewski
 *
 */
public class ResultsAR extends ResultsBase {
	public double[] arrayOfARUp;
	public double[] arrayOfARDown;
	
	public ResultsAR(int length){
		super(length);
		this.arrayOfARUp = new double[length];
		this.arrayOfARDown = new double[length];
	}
}
