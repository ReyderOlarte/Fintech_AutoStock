package com.autoStock.order;

import com.autoStock.Co;
import com.autoStock.order.OrderDefinitions.OrderType;
import com.autoStock.trading.types.Order;

/**
 * @author Kevin Kowalewski
 *
 */
public class OrderWatcher {
	private Thread threadForWatcher;
	private Order order;
	private final int maxWaitPeriod = 3;
	
	public OrderWatcher(Order order){
		this.order = order;
	}
	
	public void startWatching(){
		threadForWatcher = new Thread(new Runnable(){
			@Override
			public void run(){
				int waitedPeriods = 0;
				
				while (true){
					try {Thread.sleep(1000*30);}catch(InterruptedException e){return;}
					if (order.orderType == OrderType.order_long_entry || order.orderType == OrderType.order_short_entry) {
						
						if (waitedPeriods >= maxWaitPeriod){
							Co.println("--> Order cancelled due to not being filled!: " + order.symbol.name + ", " + waitedPeriods);
							order.cancelOrder();
						}else{
							Co.println("--> Order not yet success, watching: " + order.symbol.name + ", " + waitedPeriods);	
						}
					} else {
						Co.println("--> Order success or watcher not needed, ending watcher: " + order.symbol.name);
						threadForWatcher.interrupt();
						return;
					}

					waitedPeriods++;
				}
			}
		});

		threadForWatcher.start();
	}
	
	public void stopWatching(){
		threadForWatcher.interrupt();
	}
}
