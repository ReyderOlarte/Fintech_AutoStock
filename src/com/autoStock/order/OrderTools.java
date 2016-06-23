package com.autoStock.order;

import com.autoStock.exchange.results.ExResultMarketOrder.ExResultSetMarketOrder;
import com.autoStock.order.OrderDefinitions.IbOrderStatus;

/**
 * @author Kevin Kowalewski
 *
 */
public class OrderTools {
	public IbOrderStatus getOrderStatus(ExResultSetMarketOrder exResultSetMarketOrder){
		return exResultSetMarketOrder.listOfExResultRowMarketOrder.get(exResultSetMarketOrder.listOfExResultRowMarketOrder.size()-1).status;
	}
	
	public double getOrderAverageFillPrice(ExResultSetMarketOrder exResultSetMarketOrder){
		return exResultSetMarketOrder.listOfExResultRowMarketOrder.get(exResultSetMarketOrder.listOfExResultRowMarketOrder.size()-1).priceAvgFill;		
	}
	
	public int getOrderUnitsFilled(ExResultSetMarketOrder exResultSetMarketOrder){
		return exResultSetMarketOrder.listOfExResultRowMarketOrder.get(exResultSetMarketOrder.listOfExResultRowMarketOrder.size()-1).filledUnits;
	}

	public double getOrderComission(ExResultSetMarketOrder exResultSetMarketOrder) {
		return exResultSetMarketOrder.listOfExResultRowMarketOrder.get(exResultSetMarketOrder.listOfExResultRowMarketOrder.size()-1).comission;
	}
}
