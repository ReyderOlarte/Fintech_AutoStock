/**
 * 
 */
package com.autoStock.types;

import java.util.Date;

import com.autoStock.tools.ReflectionTools;
import com.autoStock.tools.StringTools;
import com.autoStock.tools.ThreadTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;

/**
 * @author Kevin Kowalewski
 *
 */
public class QuoteSlice implements Cloneable {
	public Symbol symbol;
	public double priceOpen;
	public double priceHigh;
	public double priceLow;
	public double priceClose;
	public double priceBid;
	public double priceAsk;
	public int sizeVolume;
	public Date dateTime = new Date();
	public Resolution resolution;
	
	public QuoteSlice(){
		
	}
	
	public QuoteSlice(Symbol symbol){
		this.symbol = symbol;
	}
	
	public QuoteSlice(Symbol symbol, double priceOpen, double priceHigh, double priceLow, double priceClose, double priceBid, double priceAsk, int sizeVolume, Date dateTime, Resolution resolution) {
		this.symbol = symbol;
		this.priceOpen = priceOpen;
		this.priceHigh = priceHigh;
		this.priceLow = priceLow;
		this.priceClose = priceClose;
		this.priceBid = priceBid;
		this.priceAsk = priceAsk;
		this.sizeVolume = sizeVolume;
		this.dateTime = dateTime;
	}
	
	@Override
	public String toString() {
		return StringTools.listOfStringToString(new ReflectionTools().getValuesToStringArryay(this), true);
	}
	
//	@Override
//	public QuoteSlice clone(){
//		try {
//			return (QuoteSlice) super.clone();
//		} catch (CloneNotSupportedException e) {
//			e.printStackTrace();
//			ApplicationStates.shutdown();
//			return null;
//		}
//	}
}
