package com.autoStock.trading.yahoo;

import com.autoStock.exchange.request.listener.RequestListenerBase;

/**
 * @author Kevin Kowalewski
 *
 */
public interface RequestFundamentalsListener extends RequestListenerBase {
	public void success(FundamentalData fundamentalData);
}
