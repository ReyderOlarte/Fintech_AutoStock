package com.autoStock.trading.platform.ib.definitions;

import com.autoStock.trading.platform.ib.core.TickType;

/**
 * @author Kevin Kowalewski
 *
 */
public class MarketDataDefinitions {
	
	public static enum TickTypes {
		type_size,
		type_price,
		type_string,
		type_generic,
	}
	
	public static enum TickPriceFields {
		field_bid(1),
		field_ask(2),
		field_last(4),
		field_high(6),
		field_low(7),
		field_close(9),
		field_open(14),
		field_week_13_low(15),
		field_week_13_high(16),
		field_week_26_low(17),
		field_week_26_high(18),
		field_week_52_high(19),
		field_week_52_low(20),
		field_auction_price(35),
		;
		
		public int field;
		public String value;
		
		TickPriceFields(int field){
			this.field = field;
		}
	}
	
	public static enum TickSizeFields {
		field_bid(0),
		field_ask(3),
		field_last_size(5),
		field_volume(8),
		field_avg_volume(21),
		field_action_volume(34),
		field_auction_imbalance(36),
		field_option_call_volume(29),
		field_option_call_open_interest(27),
		field_option_put_volume(30),
		field_option_put_open_interest(28),
		;
		
		public int field;
		
		TickSizeFields(int field){
			this.field = field;
		}
	}
	
	public static TickPriceFields getTickPriceField(int field){
		for (TickPriceFields tickPriceField : TickPriceFields.values()){
			if (tickPriceField.field == field){
				return tickPriceField;
			}
		}
		
		throw new UnsatisfiedLinkError("Could not find field for: " + TickType.getField(field) + "," + field);
	}
	
	public static TickSizeFields getTickSizeField(int field){
		for (TickSizeFields tickSizeField : TickSizeFields.values()){
			if (tickSizeField.field == field){
				return tickSizeField;
			}
		}
		
		throw new UnsatisfiedLinkError("Could not find field for: " + TickType.getField(field) + "," + field);
	}
}
