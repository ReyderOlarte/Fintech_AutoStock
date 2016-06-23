package com.autoStock.order;

import com.autoStock.order.OrderDefinitions.OrderStatus;
import com.autoStock.trading.types.Order;

/**
 * @author Kevin Kowalewski
 *
 */
public interface OrderStatusListener {
	public void orderStatusChanged(Order order, OrderStatus orderStatus);
}
