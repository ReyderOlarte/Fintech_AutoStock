/**
 * 
 */
package com.autoStock.quote;

import java.util.Date;

/**
 * @author Kevin Kowalewski
 *
 */
public class QuoteEntry {
	public long id;
	public String symbol;
	public float priceOpen;
	public float priceHigh;
	public float priceLow;
	public float priceClose;
	public int sizeVolume;
	public Date dateTime;
	
	public QuoteEntry(long id, String symbol, float priceOpen, float priceHigh, float priceLow, float priceClose, int sizeVolume, Date dateTime) {
		this.id = id;
		this.symbol = symbol;
		this.priceOpen = priceOpen;
		this.priceHigh = priceHigh;
		this.priceLow = priceLow;
		this.priceClose = priceClose;
		this.sizeVolume = sizeVolume;
		this.dateTime = dateTime;
	}
}
