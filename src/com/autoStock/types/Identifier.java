/**
 * 
 */
package com.autoStock.types;

/**
 * @author Kevin
 *
 */
public class Identifier {
	public Exchange exchange;
	public Symbol symbol;
	
	public Identifier(Exchange exchange, Symbol symbol) {
		this.exchange = exchange;
		this.symbol = symbol;
	}
	
	public Identifier(String exchange, String symbol){
		this.exchange = new Exchange(exchange);
		this.symbol = new Symbol(symbol);
	}
}
