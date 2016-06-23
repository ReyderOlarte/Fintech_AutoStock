/**
 * 
 */
package com.autoStock.trading.types;

import com.autoStock.types.Exchange;
import com.autoStock.types.Index;

/**
 * @author Kevin Kowalewski
 *
 */
public class MarketIndexData implements Cloneable {
	public Exchange exchange;
	public Index index;
	
	public MarketIndexData(Exchange exchange, Index index){
		this.exchange = exchange;
		this.index = index;
	}
	
	public MarketIndexData clone(){
		try {
			return (MarketIndexData) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
