package com.autoStock.indicator;

import java.util.Date;
import java.util.List;

import com.autoStock.tools.ArrayTools;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 * 
 */
public class CommonAnalysisData {
	public double[] arrayOfPriceOpen;
	public double[] arrayOfPriceHigh;
	public double[] arrayOfPriceLow;
	public double[] arrayOfPriceClose;
	public double[] arrayOfPriceBid;
	public double[] arrayOfPriceAsk;
	public int[] arrayOfSizeVolume;
	public Date[] arrayOfDates;
	
	private boolean isInitialized = false;

	public void setAnalysisData(List<QuoteSlice> listOfQuoteSlice) {
		if (isInitialized == false){
			arrayOfPriceOpen = new double[listOfQuoteSlice.size()];
			arrayOfPriceHigh = new double[listOfQuoteSlice.size()];
			arrayOfPriceLow = new double[listOfQuoteSlice.size()];
			arrayOfPriceClose = new double[listOfQuoteSlice.size()];
			arrayOfPriceBid = new double[listOfQuoteSlice.size()];
			arrayOfPriceAsk = new double[listOfQuoteSlice.size()];
			arrayOfSizeVolume = new int[listOfQuoteSlice.size()];
			arrayOfDates = new Date[listOfQuoteSlice.size()];
			isInitialized = true;
		}
		
		extractDataFromQuoteSlice(listOfQuoteSlice, null, arrayOfPriceOpen, arrayOfPriceHigh, arrayOfPriceLow, arrayOfPriceClose, arrayOfPriceBid, arrayOfPriceAsk, arrayOfSizeVolume, arrayOfDates);
	}

	private void extractDataFromQuoteSlice(List<QuoteSlice> listOfQuoteSlice, String field, double[] arrayOfPriceOpen, double[] arrayOfPriceHigh, double[] arrayOfPriceLow, double[] arrayOfPriceClose, double[] arrayOfPriceBid, double[] arrayOfPriceAsk, int[] arrayOfSizeVolume, Date[] arrayOfDates) {
		int i = 0;

		for (QuoteSlice quoteSlice : listOfQuoteSlice) {
			arrayOfPriceOpen[i] = quoteSlice.priceOpen;
			arrayOfPriceHigh[i] = quoteSlice.priceHigh;
			arrayOfPriceLow[i] = quoteSlice.priceLow;
			arrayOfPriceClose[i] = quoteSlice.priceClose;
			arrayOfPriceBid[i] = quoteSlice.priceBid;
			arrayOfPriceAsk[i] = quoteSlice.priceAsk;
			arrayOfSizeVolume[i] = quoteSlice.sizeVolume;
			arrayOfDates[i] = quoteSlice.dateTime;
			i++;
		}
	}

	public void reset() {
		 isInitialized = false;
	}

	public int length() {
		return arrayOfDates.length;
	}

	public void remove(int element) {
		arrayOfPriceOpen = ArrayTools.removeElement(arrayOfPriceOpen, element);
		arrayOfPriceHigh = ArrayTools.removeElement(arrayOfPriceHigh, element);
		arrayOfPriceLow = ArrayTools.removeElement(arrayOfPriceLow, element);
		arrayOfPriceClose = ArrayTools.removeElement(arrayOfPriceClose, element);
		arrayOfPriceBid = ArrayTools.removeElement(arrayOfPriceBid, element);
		arrayOfPriceAsk = ArrayTools.removeElement(arrayOfPriceAsk, element);
		arrayOfSizeVolume = ArrayTools.removeElement(arrayOfSizeVolume, element);
		arrayOfDates = ArrayTools.removeElement(arrayOfDates, element);
	}
}
