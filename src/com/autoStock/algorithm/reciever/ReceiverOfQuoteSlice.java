/**
 * 
 */
package com.autoStock.algorithm.reciever;

import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public interface ReceiverOfQuoteSlice {
	void receiveQuoteSlice(QuoteSlice quoteSlice);
	void endOfFeed(Symbol symbol);
}
