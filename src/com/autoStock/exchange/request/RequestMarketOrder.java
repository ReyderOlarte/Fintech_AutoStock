/**
 * 
 */
package com.autoStock.exchange.request;

import com.autoStock.Co;
import com.autoStock.exchange.ExchangeController;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestMarketOrderListener;
import com.autoStock.exchange.results.ExResultMarketOrder;
import com.autoStock.exchange.results.ExResultMarketOrder.ExResultRowMarketOrder;
import com.autoStock.exchange.results.ExResultMarketOrder.ExResultSetMarketOrder;
import com.autoStock.order.OrderDefinitions.OrderType;
import com.autoStock.trading.types.Order;
import com.autoStock.types.Exchange;

/**
 * @author Kevin Kowalewski
 *
 */
public class RequestMarketOrder {
	private ExResultSetMarketOrder exResultSetMarketOrder;
	private RequestHolder requestHolder;
	private Order order;
	private Thread threadForExecution;
	
	public RequestMarketOrder(RequestHolder requestHolder, Order order, Exchange exchange){
		this.requestHolder = requestHolder;
		this.requestHolder.caller = this;
		this.order = order;
		this.exResultSetMarketOrder = new ExResultMarketOrder(). new ExResultSetMarketOrder(order);
	}
	
	public void execute(){
		threadForExecution = new Thread(new Runnable(){
			@Override
			public void run() {
				Co.println("--> About to execute order: " + requestHolder.requestId + ", " + order.orderType.name() + ", " + order.symbol.name);
				if (order.orderType == OrderType.order_long_entry){
					ExchangeController.getIbExchangeInstance().placeLongEntry(order, requestHolder);
				}else if (order.orderType == OrderType.order_long_exit){
					ExchangeController.getIbExchangeInstance().placeLongExit(order, requestHolder);
				}else if (order.orderType == OrderType.order_short_entry){
					 ExchangeController.getIbExchangeInstance().placeShortEntry(order, requestHolder);
				}else if (order.orderType == OrderType.order_short_exit){
					 ExchangeController.getIbExchangeInstance().placeShortExit(order, requestHolder);
				}else{
					throw new UnsupportedOperationException();
				}	
			}
		});
		
		threadForExecution.start();
	}
	
	public synchronized void addResult(ExResultRowMarketOrder exResultRowMarketOrder){
		exResultSetMarketOrder.listOfExResultRowMarketOrder.add(exResultRowMarketOrder);
		((RequestMarketOrderListener)requestHolder.callback).receivedChange(requestHolder, exResultRowMarketOrder);
	}
	
	public synchronized void finished(){
		((RequestMarketOrderListener)requestHolder.callback).completed(requestHolder, exResultSetMarketOrder);
	}
	
	public void cancel(){
		try {threadForExecution.interrupt();}catch(Exception e){}
		ExchangeController.getIbExchangeInstance().cancelMarketOrder(requestHolder);
	}
}
