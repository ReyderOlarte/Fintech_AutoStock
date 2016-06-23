package com.autoStock.exchange;

import com.autoStock.types.basic.Time;

/**
 * @author Kevin Kowalewski
 *
 */
public interface ExchangeStatusListener {
	public static enum ExchangeState { //TODO: Fix this...
		status_open(new Time()),
		status_closed(new Time()),
		status_open_future(new Time()),
		status_close_future(new Time()),
		status_unknown(new Time()),
		;
		
		public Time timeUntilFuture = null;
		
		public ExchangeState setTime(Time timeUntilFuture){
			this.timeUntilFuture = timeUntilFuture;
			return this;
		}
		
		private ExchangeState(Time timeUntilFuture) {
			this.timeUntilFuture = timeUntilFuture;
		}
	}
	
	public void stateChanged(ExchangeState exchangeState);
}
