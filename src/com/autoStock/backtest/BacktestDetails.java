package com.autoStock.backtest;

import java.util.ArrayList;

/**
 * @author Kevin Kowalewski
 *
 */
public class BacktestDetails {
	double accountBalance;
	double accountTrades;
	double tradesEntry;
	double tradesReentry;
	double tradesExit;
	
	double tradesWon;
	double tradesLost;
	
	ArrayList<DescriptorForGuage> listOfDescriptorForGuage = new ArrayList<DescriptorForGuage>();
	
	public static class DescriptorForGuage {
		String signalBoundsPoint;
		String signalBoundsType;
		String signalBoundsName;
		double signalBoundsThreshold;
	}
	
	public static class DescriptorForAdjustment {
		String adjustmentType;
		String adjustmentDescription;
		String adjustmentValue;
	}
}
