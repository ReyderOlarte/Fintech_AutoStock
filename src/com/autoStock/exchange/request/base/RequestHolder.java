package com.autoStock.exchange.request.base;

import com.autoStock.exchange.request.RequestManager;

/**
 * @author Kevin Kowalewski
 *
 */
public class RequestHolder {
	public final int requestId;
	public final Object callback;
	public Object caller;
	public int mulitpleRequests = 0;
	
	public RequestHolder(){
		this.requestId = RequestManager.getNewRequestId();
		this.callback = null;
	}
	
	public RequestHolder(Object callback){
		this.requestId = RequestManager.getNewRequestId();
		this.callback = callback;
		RequestManager.addRequestHolder(this);
	}
	
	public RequestHolder(int requestId, Object callback){
		RequestManager.setMinRequestId(requestId+1);
		RequestManager.addRequestHolder(this);
		this.requestId = requestId;
		this.callback = callback;
	}
}
