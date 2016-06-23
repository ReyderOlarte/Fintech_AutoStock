/**
 * 
 */
package com.autoStock.trading.types;

/**
 * @author Kevin Kowalewski
 *
 */
public class RealtimeData implements Cloneable {

	public String symbol;
	public String securityType;
	
	public RealtimeData(String symbol, String securityType){
		this.symbol = symbol;
		this.securityType = securityType.toUpperCase();
	}

	public RealtimeData clone(){
		try {
			return (RealtimeData) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
