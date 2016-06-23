/**
 * 
 */
package com.autoStock.indicator.results;


/**
 * @author Kevin Kowalewski
 *
 */
public class ResultsBB extends ResultsBase {
	public double[] arrayOfUpperBand;
	public double[] arrayOfMiddleBand;
	public double[] arrayOfLowerBand;
	
	public ResultsBB(int length){
		super(length);
		this.arrayOfUpperBand = new double[length];
		this.arrayOfMiddleBand = new double[length];
		this.arrayOfLowerBand = new double[length];
	}
}
