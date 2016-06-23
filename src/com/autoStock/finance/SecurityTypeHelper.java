package com.autoStock.finance;

/**
 * @author Kevin Kowalewski
 *
 */
public class SecurityTypeHelper {
	public static enum SecurityType{
		type_stock("STK"),
		type_bond("BOND"),
		type_forex("FX")
		;
		
		public String ibStringName;
		
		SecurityType(String ibStringName){
			this.ibStringName = ibStringName;
		}
	}
}
