/**
 * 
 */
package com.autoStock.exchange.results;

import java.util.ArrayList;

import com.autoStock.order.OrderDefinitions.IbOrderStatus;
import com.autoStock.trading.types.Order;

/**
 * @author Kevin Kowalewski
 *
 */
public class ExResultMarketOrder {
	public class ExResultSetMarketOrder {
		public Order order;
		public ArrayList<ExResultRowMarketOrder> listOfExResultRowMarketOrder = new ArrayList<ExResultRowMarketOrder>();
		
		public ExResultSetMarketOrder(Order order){
			this.order = order;
		}
	}
	
	public static class ExResultRowMarketOrder{
		public double priceAvgFill;
		public double priceLastFill;
		public double comission;
		public int filledUnits;
		public int remainingUnits;
		public int units;
		public IbOrderStatus status;
		
		public ExResultRowMarketOrder(double priceAvgFill, double priceLastFill, double comission, int filledUnits, int remainingUnits, int units, IbOrderStatus status) {
			this.priceAvgFill = priceAvgFill;
			this.priceLastFill = priceLastFill;
			this.comission = comission;
			this.filledUnits = filledUnits;
			this.remainingUnits = remainingUnits;
			this.units = units;
			this.status = status;
		}
	}	
}
