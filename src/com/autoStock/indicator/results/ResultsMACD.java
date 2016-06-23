/**
 * 
 */
package com.autoStock.indicator.results;


/**
 * @author Kevin Kowalewski
 *
 */
public class ResultsMACD extends ResultsBase {
	public double[] arrayOfMACD;
	public double[] arrayOfMACDSignal;
	public double[] arrayOfMACDHistogram;
	
	public ResultsMACD(int length){
		super(length);
		this.arrayOfMACD = new double[length];
		this.arrayOfMACDSignal = new double[length];
		this.arrayOfMACDHistogram = new double[length];
	}
}
