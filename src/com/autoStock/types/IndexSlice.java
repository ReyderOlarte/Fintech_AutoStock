package com.autoStock.types;

import java.util.Date;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndexSlice {
	public Index index;
	public Date dateTime;
	public double valueOpen;
	public double valueHigh;
	public double valueLow;
	public double valueClose;
	public double sizeVolume;
	
	public IndexSlice(){}
	
	public IndexSlice(Index index, double valueOpen, double valueHigh, double valueLow, double valueClose, double sizeVolume) {
		this.index = index;
		this.valueOpen = valueOpen;
		this.valueHigh = valueHigh;
		this.valueLow = valueLow;
		this.valueClose = valueClose;
		this.sizeVolume = sizeVolume;
	}
}
