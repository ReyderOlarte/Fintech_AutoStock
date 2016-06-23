/**
 * 
 */
package com.autoStock.types;

import com.autoStock.scanner.Shortlist.ShortlistReason;

/**
 * @author Kevin Kowalewski
 *
 */
public class ShorlistOfStock {
	public String symbol;
	public String exchange;
	public ShortlistReason shortlistReason;
	
	public ShorlistOfStock(String symbol, String exchange, ShortlistReason shortlistReason) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.shortlistReason = shortlistReason;
	}
}
