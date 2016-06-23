package com.autoStock.trading.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.autoStock.Co;
import com.autoStock.account.TransactionFees;
import com.autoStock.exchange.request.OrderIdProvider;
import com.autoStock.exchange.request.RequestMarketOrder;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestMarketOrderIdListener;
import com.autoStock.exchange.request.listener.RequestMarketOrderListener;
import com.autoStock.exchange.results.ExResultMarketOrder.ExResultRowMarketOrder;
import com.autoStock.exchange.results.ExResultMarketOrder.ExResultSetMarketOrder;
import com.autoStock.order.OrderDefinitions.IbOrderStatus;
import com.autoStock.order.OrderDefinitions.OrderMode;
import com.autoStock.order.OrderDefinitions.OrderStatus;
import com.autoStock.order.OrderDefinitions.OrderType;
import com.autoStock.order.OrderSimulator;
import com.autoStock.order.OrderStatusListener;
import com.autoStock.order.OrderTable;
import com.autoStock.order.OrderTools;
import com.autoStock.order.OrderValue;
import com.autoStock.order.OrderWatcher;
import com.autoStock.tools.DateTools;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 * 
 */
public class Order {
	public Symbol symbol;
	public Exchange exchange;
	public Position position;
	private final int unitsRequested;
	private double priceRequested = 0;
	private double priceFilled = 0;
	private OrderStatus orderStatus = OrderStatus.none;
	public OrderType orderType = OrderType.none;
	public final OrderMode orderMode;
	private RequestMarketOrder requestMarketOrder;
	private OrderStatusListener orderStatusListener;
	private OrderTools orderTools = new OrderTools();
	private AtomicInteger atomicIntForUnitsFilled = new AtomicInteger();
	private OrderTable orderTable = new OrderTable();
	private OrderWatcher orderWatcher = new OrderWatcher(this);

	public Order(Symbol symbol, Exchange exchange, Position position, OrderType orderType, OrderMode orderMode, int unitsRequested, double priceRequested, OrderStatusListener orderStatusListener) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.position = position;
		this.unitsRequested = unitsRequested;
		this.priceRequested = priceRequested;
		this.orderType = orderType;
		this.orderMode = orderMode;
		this.orderStatusListener = orderStatusListener;
	}

	public void executeOrder() {
		if (orderMode == OrderMode.mode_exchange) {
			Co.println("--> Executing order with mode: " + orderMode.name() + ", " + position.positionType.name() + ", " + orderType.name());
		}

		if (orderStatus == OrderStatus.none) {
			if (orderMode == OrderMode.mode_exchange) {
				orderStatus = OrderStatus.status_presubmit;
				OrderIdProvider.getInstance().getNextOrderId(new RequestMarketOrderIdListener() {
					@Override
					public void failed(RequestHolder requestHolder) {
						throw new IllegalStateException("Could not get next order id...");
					}

					@Override
					public void completed(int orderId) {
						submitOrder(orderId);
					}
				});
			} else {
				new OrderSimulator(this).simulateOrderFill();
			}
		} else {
			throw new IllegalStateException();
		}
	}

	private void submitOrder(int orderId) {
		requestMarketOrder = new RequestMarketOrder(new RequestHolder(new RequestMarketOrderListener() {
			@Override
			public void failed(RequestHolder requestHolder) {
				Co.println("--> Order failed");
			}

			@Override
			public void receivedChange(RequestHolder requestHolder, ExResultRowMarketOrder exResultRowMarketOrder) {
				Co.println("--> Received order: " + exResultRowMarketOrder.status.name());

				ArrayList<String> columnValues = new ArrayList<String>();
				columnValues.add(DateTools.getPretty(new Date()));
				columnValues.add(symbol.name);
				columnValues.add(orderType.name());
				columnValues.add(exResultRowMarketOrder.status.name());
				columnValues.add(String.valueOf(unitsRequested));
				columnValues.add(String.valueOf(exResultRowMarketOrder.remainingUnits));
				columnValues.add(String.valueOf(exResultRowMarketOrder.filledUnits));
				columnValues.add(String.valueOf(priceRequested));
				columnValues.add(String.valueOf(exResultRowMarketOrder.priceAvgFill));
				columnValues.add(String.valueOf(exResultRowMarketOrder.priceLastFill));
				orderTable.addRow(columnValues);
				orderTable.display();

				if (exResultRowMarketOrder.filledUnits > 0 && exResultRowMarketOrder.remainingUnits > 0 && exResultRowMarketOrder.status == IbOrderStatus.status_submitted) {
					orderStatus = OrderStatus.status_filled_partially;
				}
			}

			@Override
			public void completed(RequestHolder requestHolder, ExResultSetMarketOrder exResultSetMarketOrder) {
				double unitPriceFilledAverage = orderTools.getOrderAverageFillPrice(exResultSetMarketOrder);
				int unitsFilled = orderTools.getOrderUnitsFilled(exResultSetMarketOrder);
				IbOrderStatus ibOrderStatus = orderTools.getOrderStatus(exResultSetMarketOrder);

				if (ibOrderStatus == IbOrderStatus.status_filled) {
					if (orderStatus == OrderStatus.status_filled) {
						Co.println("--> Order is already filled!");
					} else if (orderStatus == OrderStatus.status_presubmit || orderStatus == OrderStatus.status_submitted || orderStatus == OrderStatus.status_filled_partially) {
						Co.println("\n\n--> Order completed at price: " + unitPriceFilledAverage);
						Co.println("--> Order status: " + ibOrderStatus.name());
						orderWatcher.stopWatching();
						orderUnitsFilled(unitPriceFilledAverage, unitsFilled);
					} else {
						Co.println("--> Warning: Order status was cancelled, but order was completed!");
						//throw new IllegalStateException("Order status did not match: " + orderStatus.name());
					}
				} else if (ibOrderStatus == IbOrderStatus.status_cancelled) {
					if (orderStatus != OrderStatus.status_cancelled) {
						orderStatus = OrderStatus.status_cancelled;
						orderWatcher.stopWatching();
						orderStatusListener.orderStatusChanged(Order.this, orderStatus);
					} else {
						Co.println("--> Order is already cancelled");
					}
				}
			}

			@Override
			public void failed(IbOrderStatus ibOrderStatus) {
				if (ibOrderStatus == IbOrderStatus.status_cancelled) {
					orderStatus = OrderStatus.status_cancelled;
				}
				Co.println("--> Failed with ibOrderStatus: " + ibOrderStatus.name());
			}
		}), this, exchange);

		requestMarketOrder.execute();
		orderWatcher.startWatching();
	}

	public void cancelOrder() {
		if (orderStatus == OrderStatus.status_filled) {
			throw new IllegalStateException("Can't cancel a filled order");
		}
		if (requestMarketOrder == null) {
			// Could be a problem, depends if order is simulated
		} else {
			requestMarketOrder.cancel();
		}
	}

	public void orderUnitsFilled(double priceAverageFill, int units) {
		atomicIntForUnitsFilled.addAndGet(units);
		// Co.println("--> Order units filled: " + symbol.symbolName + ", " + units + " of " + unitsRequested);

		if (atomicIntForUnitsFilled.get() == unitsRequested) {
			orderStatus = OrderStatus.status_filled;
			if (orderType == OrderType.order_long_entry) {
				orderType = OrderType.order_long;
			} else if (orderType == OrderType.order_short_entry) {
				orderType = OrderType.order_short;
			} else if (orderType == OrderType.order_long_exit) {
				orderType = OrderType.order_long_exited;
			} else if (orderType == OrderType.order_short_exit) {
				orderType = OrderType.order_short_exited;
			} else {
				throw new IllegalStateException();
			}

			priceFilled = priceAverageFill;
			if (priceRequested == 0) {
				priceRequested = priceFilled;
			}

			orderStatusListener.orderStatusChanged(this, orderStatus);
		} else {
			orderStatus = OrderStatus.status_filled_partially;
		}
	}

	public OrderValue getOrderValue() {
		if (atomicIntForUnitsFilled.get() == 0) {
			// Co.println("--> Warning filled units is 0..." + symbol.symbolName);
		}

		return new OrderValue(getRequestedValue(false), getFilledValue(false), getIntrinsicValue(false), 
				getRequestedValue(true), getFilledValue(true), getIntrinsicValue(true), 
				getRequestedPrice(true), getFilledPrice(true), getIntrinsicPrice(true), 
				getUnitPriceRequested(), getUnitPriceFilled(), getUnitPriceIntrinsic(), 
				TransactionFees.getTransactionCost(getUnitsIntrinsic(), priceRequested));
	}

	private double getFilledPrice(boolean includeTransactionFees) {
		double transactionFees = TransactionFees.getTransactionCost(getUnitsFilled(), priceFilled);
		double positionValue = getUnitsFilled() * priceFilled;
		double total = positionValue + (includeTransactionFees ? transactionFees : 0);

		return total;
	}

	private double getFilledValue(boolean includeTransactionFees) {
		double transactionFees = TransactionFees.getTransactionCost(getUnitsFilled(), priceFilled);
		double positionValue = getUnitsFilled() * priceFilled;
		double total = positionValue - (includeTransactionFees ? transactionFees : 0);

		return total;
	}

	private double getRequestedPrice(boolean includeTransactionFees) {
		double transactionFees = TransactionFees.getTransactionCost(getUnitsFilled(), priceRequested);
		double positionValue = getUnitsFilled() * priceRequested;
		double total = positionValue + (includeTransactionFees ? transactionFees : 0);

		return total;
	}

	private double getRequestedValue(boolean includeTransactionFees) {
		double transactionFees = TransactionFees.getTransactionCost(getUnitsFilled(), priceRequested);
		double positionValue = getUnitsFilled() * priceRequested;
		double total = positionValue - (includeTransactionFees ? transactionFees : 0);

		return total;
	}

	private double getIntrinsicPrice(boolean includeTransactionFees) {
		if (orderStatus == OrderStatus.status_filled) {
			return getFilledPrice(includeTransactionFees);
		} else if (orderStatus == OrderStatus.status_filled_partially) {
			return getRequestedPrice(includeTransactionFees);
		} else {
			return getRequestedPrice(includeTransactionFees);
		}
	}

	private double getIntrinsicValue(boolean includeTransactionFees) {
		if (orderStatus == OrderStatus.status_filled) {
			return getFilledValue(includeTransactionFees);
		} else if (orderStatus == OrderStatus.status_filled_partially) {
			return getRequestedValue(includeTransactionFees);
		} else {
			return getRequestedValue(includeTransactionFees);
		}
	}

	public int getUnitsRequested() {
		return unitsRequested;
	}

	public int getUnitsFilled() {
		return atomicIntForUnitsFilled.get();
	}

	public int getUnitsIntrinsic() {
		return unitsRequested;
	}

	public double getUnitPriceRequested() {
		return priceRequested;
	}

	public double getUnitPriceFilled() {
		return priceFilled;
	}

	public double getUnitPriceIntrinsic() {
		if (orderStatus == OrderStatus.status_filled) {
			return priceFilled;
		} else {
			return priceRequested;
		}
	}

	public double getTransactionFees() {
		return TransactionFees.getTransactionCost(unitsRequested, priceRequested);
	}
}
