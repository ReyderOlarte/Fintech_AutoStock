package com.autoStock.exchange.request.listener;

import com.autoStock.exchange.results.MultipleResultMarketScanner.MultipleResultSetMarketScanner;

/**
 * @author Kevin Kowalewski
 *
 */
public interface MultipleRequestMarketScannerListener extends RequestListenerBase {
	public void completed(MultipleResultSetMarketScanner multipleResultSetMarketScanner);
}
