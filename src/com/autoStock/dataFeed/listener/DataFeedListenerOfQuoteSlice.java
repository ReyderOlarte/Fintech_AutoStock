/**
 * 
 */
package com.autoStock.dataFeed.listener;

import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 *
 */
public interface DataFeedListenerOfQuoteSlice {
	void receivedQuoteSlice(QuoteSlice typeQuoteSlice);
	void endOfFeed();
}
