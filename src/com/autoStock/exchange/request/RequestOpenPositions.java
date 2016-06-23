package com.autoStock.exchange.request;

import com.autoStock.exchange.request.base.RequestHolder;

/**
 * @author Kevin Kowalewski
 *
 */
public class RequestOpenPositions {
	private RequestHolder requestHolder;
	
	public RequestOpenPositions(RequestHolder requestHolder){
		this.requestHolder = requestHolder;
	}
}
