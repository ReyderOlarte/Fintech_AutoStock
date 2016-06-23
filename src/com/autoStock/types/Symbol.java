package com.autoStock.types;

import com.autoStock.finance.SecurityTypeHelper.SecurityType;

/**
 * @author Kevin Kowalewski
 *
 */
public class Symbol {
	public String name;
	public SecurityType securityType;

	public Symbol(String symbol, SecurityType securityType) {
		this.name = symbol;
		this.securityType = securityType;
	}
	
	public Symbol(String symbol) {
		this.name = symbol;
		securityType = SecurityType.type_stock;
	}
	
	@Override
	public String toString() {
		return name + ", " + securityType.name();
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((Symbol)obj).name.equals(name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
