/**
 * 
 */
package com.autoStock.trading.types;

import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin
 *
 */
public class Security {
	public Symbol symbol;
	public Exchange exchange;
	
	public Security(Symbol symbol, Exchange exchange) {
		this.symbol = symbol;
		this.exchange = exchange;
	}
}
