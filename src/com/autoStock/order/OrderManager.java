package com.autoStock.order;


/**
 * @author Kevin Kowalewski
 *
 */
public class OrderManager {
	private static OrderManager instance = new OrderManager();
	
	public static OrderManager getInstance(){
		return instance;
	}
}
