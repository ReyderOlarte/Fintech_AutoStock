package com.autoStock.position;

import java.util.ArrayList;

import com.autoStock.account.TransactionFees;
import com.autoStock.order.OrderDefinitions.OrderType;
import com.autoStock.order.OrderValue;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.trading.types.Order;
import com.autoStock.trading.types.Position;

/**
 * @author Kevin Kowalewski
 * 
 */
public class PositionUtils {
	private Position position;
	private ArrayList<Order> listOfOrder;
	private ArrayList<OrderValue> listOfOrderValue = new ArrayList<OrderValue>();
	
	// * Price is impact to bank account, value is amount once sold

	public PositionUtils(Position position, ArrayList<Order> listOfOrder) {
		if (position == null){throw new NullPointerException("Position can't be null");}
		this.position = position;
		this.listOfOrder = listOfOrder;
		synchronized (listOfOrder) {
			for (Order order : listOfOrder) {
				listOfOrderValue.add(order.getOrderValue());
			}
		}
	}

	public int getOrderUnitsFilled() {
		synchronized (listOfOrder) {
			int units = 0;
			for (Order order : listOfOrder) {
				if (order.orderType == OrderType.order_long || order.orderType == OrderType.order_short) {
					units += order.getUnitsFilled();
				}
			}
			return units;
		}
	}

	public int getOrderUnitsRequested() {
		synchronized (listOfOrder) {
			int units = 0;
			for (Order order : listOfOrder) {
				units += order.getUnitsRequested();
			}
			return units;
		}
	}

	public int getOrderUnitsIntrinsic() {
		synchronized (listOfOrder) {
			int units = 0;
			for (Order order : listOfOrder) {
				units += order.getUnitsIntrinsic();
			}
			return units;
		}
	}

	public double getOrderTransactionFeesIntrinsic() {
		synchronized (listOfOrder) {
			double transactionFees = 0;
			for (OrderValue orderValue : listOfOrderValue) {
				transactionFees += orderValue.transactionFees;
			}
			return transactionFees;
		}
	}

	public double getOrderPriceRequested(boolean includeTransactionFees) {
		synchronized (listOfOrder) {
			double priceTotal = 0;
			for (OrderValue orderValue : listOfOrderValue) {
				priceTotal += includeTransactionFees ? orderValue.priceRequestedWithFees : orderValue.valueRequested;
			}
			return priceTotal;
		}
	}

	public double getOrderPriceFilled(boolean includeTransactionFees) {
		synchronized (listOfOrder) {
			double priceTotal = 0;
			for (OrderValue orderValue : listOfOrderValue) {
				priceTotal += includeTransactionFees ? orderValue.priceFilledWithFees : orderValue.valueFilled;
			}
			return priceTotal;
		}
	}

	public double getOrderPriceIntrinsic(boolean includeTransactionFees) {
		synchronized (listOfOrder) {
			double priceTotal = 0;
			for (OrderValue orderValue : listOfOrderValue) {
				priceTotal += includeTransactionFees ? orderValue.priceIntrinsicWithFees : orderValue.valueIntrinsic;
			}
			return priceTotal;
		}
	}

	public double getOrderValueRequested(boolean includeTransactionFees) {
		synchronized (listOfOrder) {
			double priceTotal = 0;
			for (OrderValue orderValue : listOfOrderValue) {
				priceTotal += includeTransactionFees ? orderValue.valueRequestedWithFees : orderValue.valueRequested;
			}
			return priceTotal;
		}
	}

	public double getOrderValueFilled(boolean includeTransactionFees) {
		synchronized (listOfOrder) {
			double priceTotal = 0;
			for (OrderValue orderValue : listOfOrderValue) {
				priceTotal += includeTransactionFees ? orderValue.valueFilledWithFees : orderValue.valueFilled;
			}
			return priceTotal;
		}
	}

	public double getOrderValueIntrinsic(boolean includeTransactionFees) {
		synchronized (listOfOrder) {
			double priceTotal = 0;
			for (OrderValue orderValue : listOfOrderValue) {
				priceTotal += includeTransactionFees ? orderValue.valueIntrinsicWithFees : orderValue.valueIntrinsic;
			}
			return priceTotal;
		}
	}

	public double getPositionValueCurrent(boolean includeTransactionFee) {
		synchronized (listOfOrder) {
			double priceTotal = 0;
			double unitPriceFilled = getOrderUnitPriceIntrinsic();
			double unitPriceLastKnown = position.getLastKnownUnitPrice();
			int unitsFilled = getOrderUnitsIntrinsic();
			
			if (position.positionType == PositionType.position_long_entry
				|| position.positionType == PositionType.position_long
				|| position.positionType == PositionType.position_long_exit 
				|| position.positionType == PositionType.position_long_exited){
				priceTotal = unitsFilled * unitPriceLastKnown;
			}else if (position.positionType == PositionType.position_short_entry
					|| position.positionType == PositionType.position_short
					|| position.positionType == PositionType.position_short_exit 
					|| position.positionType == PositionType.position_short_exited){
				priceTotal = unitsFilled * (unitPriceFilled + (unitPriceFilled - unitPriceLastKnown));
			}else{
				return 0; //throw new IllegalStateException("PositionType: " + position.positionType);
			}
			
			double transactionFees = TransactionFees.getTransactionCost(unitsFilled, unitPriceLastKnown);
			
			priceTotal -= (includeTransactionFee ? transactionFees : 0);
			
			return priceTotal;
		}
	}

	public double getPositionPriceCurrent(boolean includeTransactionFee) {
		synchronized (listOfOrder) {
			double unitPriceLastKnown = position.getLastKnownUnitPrice();
			double priceTotal = getOrderUnitsFilled() * unitPriceLastKnown;
			double transactionFees = TransactionFees.getTransactionCost(getOrderUnitsFilled(), unitPriceLastKnown);

			return priceTotal + (includeTransactionFee ? transactionFees : 0);
		}
	}

	public double getOrderUnitPriceRequested() {
		synchronized (listOfOrder) {
			double priceTotal = 0;
			int totalUnits = 0;
			
			for (Order order : listOfOrder){
				totalUnits += order.getUnitsRequested();
			}
			
			for (Order order : listOfOrder){
				priceTotal += ((double)order.getUnitsRequested() / totalUnits) * order.getUnitPriceRequested();
			}
			
			return priceTotal;
		}
	}

	public double getOrderUnitPriceFilled() {
		synchronized (listOfOrder) {
			double priceTotal = 0;
			int totalUnits = 0;
			
			for (Order order : listOfOrder){
				totalUnits += order.getUnitsFilled();
			}
			
			for (Order order : listOfOrder){
				priceTotal += ((double)order.getUnitsFilled() / totalUnits) * order.getUnitPriceFilled();
			}
			
			return priceTotal;
		}
	}

	public double getOrderUnitPriceIntrinsic() {
		synchronized (listOfOrder) {
			double priceTotal = 0;
			int totalUnits = 0;
			
			for (Order order : listOfOrder){
				totalUnits += order.getUnitsIntrinsic();
			}
			
			for (Order order : listOfOrder){
				priceTotal += ((double)order.getUnitsIntrinsic() / totalUnits) * order.getUnitPriceIntrinsic();
			}
			
			return priceTotal;
		}
	}
}
