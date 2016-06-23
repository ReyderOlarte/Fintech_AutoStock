package com.autoStock.order;

/**
 * @author Kevin Kowalewski
 *
 */
public class OrderDefinitions {
	public static enum OrderMode {
		mode_simulated,
		mode_exchange,
		none,
	}
	
	public static enum OrderType {
		order_long_entry,
		order_short_entry,
		order_long,
		order_short,
		order_long_exit,
		order_short_exit,
		order_long_exited,
		order_short_exited,
		
		order_failed,
		none,
		;
	}
	
	public static enum OrderStatus {
		status_presubmit,
		status_submitted,
		status_filled_partially,
		status_filled,
		status_failed,
		status_cancelled,
		none,
	}
	
	public static enum IbOrderStatus {
		status_pending_submit("PendingSubmit"),
		status_pending_cancel("PendingCancel"),
		status_pre_submit("PreSubmitted"),
		status_submitted("Submitted"),
		status_filled("Filled"),
		status_cancelled("Cancelled"),
		status_inactive("Inactive"),
		unknown("unknown"),
		none("none"),
		;
		
		private String stringForIbStatus;
		
		IbOrderStatus(String string){
			this.stringForIbStatus = string;
		}
		
		public static synchronized IbOrderStatus getIbOrderStatus(String status){
			for (IbOrderStatus ibOrderStatus : IbOrderStatus.values()){
				if (ibOrderStatus.stringForIbStatus.equals(status)){
					return ibOrderStatus;
				}
			}
			
			throw new UnsupportedOperationException();
		}
	}
}
