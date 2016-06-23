package com.autoStock.indicator.candleStick;

import java.util.Date;

import com.autoStock.indicator.candleStick.CandleStickDefinitions.CandleStickIdentity;

/**
 * @author Kevin Kowalewski
 *
 */
public class CandleStickIdentifierResult {
	public CandleStickIdentity candleStickType;
	public Date[] arrayOfDates;
	public int[] arrayOfMatches;
	
	public CandleStickIdentifierResult(CandleStickIdentity candleStickType, Date[] arrayOfDates, int[] arrayOfMatches) {
		this.candleStickType = candleStickType;
		this.arrayOfDates = arrayOfDates;
		this.arrayOfMatches = arrayOfMatches;
	}
	
	public int getLastValue(){
		if (arrayOfMatches[arrayOfMatches.length-1] != 0){
//			throw new UnsupportedOperationException();
		}
		return arrayOfMatches[arrayOfMatches.length-1];
	}
}
