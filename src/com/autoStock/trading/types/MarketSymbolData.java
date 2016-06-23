/**
 * 
 */
package com.autoStock.trading.types;

import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class MarketSymbolData implements Cloneable {
	public Exchange exchange;
	public Symbol symbol;
	
	public MarketSymbolData(Exchange exchange, Symbol symbol){
		this.exchange = exchange;
		this.symbol = symbol;
	}
	
	public MarketSymbolData clone(){
		try {
			return (MarketSymbolData) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
