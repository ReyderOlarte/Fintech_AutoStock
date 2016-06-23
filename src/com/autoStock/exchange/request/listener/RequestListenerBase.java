package com.autoStock.exchange.request.listener;

import com.autoStock.exchange.request.base.RequestHolder;

/**
 * @author Kevin Kowalewski
 *
 */
public interface RequestListenerBase {
	public void failed(RequestHolder requestHolder);	
}
