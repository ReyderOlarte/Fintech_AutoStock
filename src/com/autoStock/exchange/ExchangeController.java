/**
 * 
 */
package com.autoStock.exchange;

import java.util.ArrayList;
import java.util.Random;

import com.autoStock.trading.platform.ib.IbExchangeInstance;

/**
 * @author Kevin Kowalewski
 *
 */
public class ExchangeController {
	public static int MAX_INSTANCES = 1;
	public static ArrayList<IbExchangeInstance> listOfIbExchangeInstance = new ArrayList<IbExchangeInstance>();
	
	public void init(){
		for (int i=0; i<MAX_INSTANCES; i++){
			IbExchangeInstance ibExchangeInstance = new IbExchangeInstance();
			ibExchangeInstance.init();
			listOfIbExchangeInstance.add(ibExchangeInstance);
		}
	}
	
	public static IbExchangeInstance getIbExchangeInstance(){
		return listOfIbExchangeInstance.get(new Random().nextInt(listOfIbExchangeInstance.size()));
	}
}
