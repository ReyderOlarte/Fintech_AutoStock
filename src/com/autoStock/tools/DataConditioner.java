/**
 * 
 */
package com.autoStock.tools;

import java.util.Random;

/**
 * @author Kevin Kowalewski
 *
 */
public class DataConditioner {
	//TODO: Implement me from AnalysisBase
	
	public PrecededDataset preceedDatasetWithPeriod(double[] arrayOfPriceOpen, double[] arrayOfPriceHigh, double[] arrayOfPriceLow, double[] arrayOfPriceClose, int periodLength, int datasetLength){
		double[] tempValuesPriceOpen = new double[datasetLength+periodLength];
		double[] tempValuesPriceHigh = new double[datasetLength+periodLength];
		double[] tempValuesPriceLow = new double[datasetLength+periodLength];
		double[] tempValuesPriceClose = new double[datasetLength+periodLength];
		
		//Co.println("periodLength: " + periodLength + "," + datasetLength);
		
		for (int i=0; i<=periodLength; i++){
			int preceedWith = new Random().nextInt(periodLength);
			if (arrayOfPriceOpen != null){tempValuesPriceOpen[i] = arrayOfPriceOpen[preceedWith];}
			if (arrayOfPriceHigh != null){tempValuesPriceHigh[i] = arrayOfPriceHigh[preceedWith];}
			if (arrayOfPriceLow != null){tempValuesPriceLow[i] = arrayOfPriceLow[preceedWith];}
			if (arrayOfPriceClose != null){tempValuesPriceClose[i] = arrayOfPriceClose[preceedWith];}
		}
		
		for (int i=periodLength; i<datasetLength+periodLength; i++){
			if (arrayOfPriceOpen != null){tempValuesPriceOpen[i] = arrayOfPriceOpen[i-periodLength];}
			if (arrayOfPriceHigh != null){tempValuesPriceHigh[i] = arrayOfPriceHigh[i-periodLength];}
			if (arrayOfPriceLow != null){tempValuesPriceLow[i] = arrayOfPriceLow[i-periodLength];}
			if (arrayOfPriceClose != null){tempValuesPriceClose[i] = arrayOfPriceClose[i-periodLength];}
		}
		
		return new PrecededDataset(tempValuesPriceOpen, tempValuesPriceHigh, tempValuesPriceLow, tempValuesPriceClose);
	}
	
	public class PrecededDataset{
		public double[] arrayOfPriceOpen;
		public double[] arrayOfPriceHigh;
		public double[] arrayOfPriceLow;
		public double[] arrayOfPriceClose;
		
		public PrecededDataset(double[] arrayOfPriceOpen, double[] arrayOfPriceHigh, double[] arrayOfPriceLow, double[] arrayOfPriceClose){
			this.arrayOfPriceOpen = arrayOfPriceOpen;
			this.arrayOfPriceHigh = arrayOfPriceHigh;
			this.arrayOfPriceLow = arrayOfPriceClose;
			this.arrayOfPriceClose = arrayOfPriceClose;
		}
	}
}
