package com.autoStock.position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.autoStock.Co;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.ListTools;
import com.autoStock.tools.MathTools;
import com.autoStock.tools.ReflectiveComparator;
import com.autoStock.tools.ReflectiveComparator.ListComparator;
import com.autoStock.tools.ReflectiveComparator.ListComparator.SortDirection;
import com.autoStock.types.basic.Time;

/**
 * @author Kevin Kowalewski
 *
 */
public class PositionHistory {
	public ArrayList<Double> listOfProfitLossPercent = new ArrayList<Double>();
	public ArrayList<PositionHistoryItem> listOfPositionHistory = new ArrayList<>();
	public Date dateOfCreation;
	public static enum ProfitOrLoss{profit, loss};
	
	public static class PositionHistoryItem {
		public double profitLossPercent;
		public double priceClose;
		public Date date;
		
		public PositionHistoryItem(double profitLossPercent, double priceClose, Date date) {
			this.profitLossPercent = profitLossPercent;
			this.priceClose = priceClose;
			this.date = date;
		}
	}
	
	public void addProfitLoss(PositionHistoryItem item){
		listOfPositionHistory.add(item);
	}
	
	public Time getAge(){
		PositionHistoryItem lastItem = listOfPositionHistory.get(0);
		PositionHistoryItem currentItem = ListTools.getLast(listOfPositionHistory);
		return DateTools.getTimeUntilDate(currentItem.date, lastItem.date);
	}
	
	public Time getTimeIn(ProfitOrLoss profitOrLoss){
		PositionHistoryItem lastItem = listOfPositionHistory.get(0);
		PositionHistoryItem currentItem = ListTools.getLast(listOfPositionHistory);
		
		for (PositionHistoryItem item : listOfPositionHistory){
			if (profitOrLoss == ProfitOrLoss.profit && item.profitLossPercent < 0){lastItem = item;}
			if (profitOrLoss == ProfitOrLoss.loss && item.profitLossPercent > 0){lastItem = item;}
		}
		
		if (lastItem == null || currentItem == null){return new Time();}
		
//		Co.println("--> " + profitOrLoss.name() + ", " + ListTools.getLast(listOfPositionHistory).date + ", P&L: " + lastItem.date);
		
		return DateTools.getTimeUntilDate(currentItem.date, lastItem.date);
	}
	
	public PositionHistoryItem getMaxPercentProfitLoss(){
		PositionHistoryItem max = null;
		for (PositionHistoryItem item : listOfPositionHistory){if (max == null || item.profitLossPercent > max.profitLossPercent){max = item;}}
		return max;
	}
	
	public PositionHistoryItem getMinPercentProfitLoss(){
		PositionHistoryItem min = null;
		for (PositionHistoryItem item : listOfPositionHistory){if (min == null || item.profitLossPercent < min.profitLossPercent){min = item;}}
		return min;
	}
}
