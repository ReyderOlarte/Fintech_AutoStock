/**
 * 
 */
package com.autoStock.indicator.results;


/**
 * @author Kevin Kowalewski
 *
 */
public class ResultsDI extends ResultsBase {	
	public double[] arrayOfDIPlus;
	public double[] arrayOfDIMinus;
	
	public ResultsDI(int length){
		super(length);
		this.arrayOfDIPlus = new double[length];
		this.arrayOfDIMinus = new double[length];
	}
}
