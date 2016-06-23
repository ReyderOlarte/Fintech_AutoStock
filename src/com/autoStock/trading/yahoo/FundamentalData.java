package com.autoStock.trading.yahoo;

import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 * 
 */
public class FundamentalData {
	public Exchange exchange;
	public Symbol symbol;

	public int avgDailyVolume;
	public int todaysVolume;
	public double dividendPerShare;
	public double dividendYeild;
	public double earningsPerShare;
	public double todaysHigh;
	public double todaysLow;
	public int marketCap;
	public double priceToEarningsRatio;
	public double shortRatio;
	
	public FundamentalData(Exchange exchange, Symbol symbol, int avgDailyVolume, int todaysVolume, double dividendPerShare, double dividendYeild, double earningsPerShare, double todaysHigh, double todaysLow, int marketCap, double priceToEarningsRatio, double shortRatio) {
		this.exchange = exchange;
		this.symbol = symbol;
		this.avgDailyVolume = avgDailyVolume;
		this.todaysVolume = todaysVolume;
		this.dividendPerShare = dividendPerShare;
		this.dividendYeild = dividendYeild;
		this.earningsPerShare = earningsPerShare;
		this.todaysHigh = todaysHigh;
		this.todaysLow = todaysLow;
		this.marketCap = marketCap;
		this.priceToEarningsRatio = priceToEarningsRatio;
		this.shortRatio = shortRatio;
	}
}
